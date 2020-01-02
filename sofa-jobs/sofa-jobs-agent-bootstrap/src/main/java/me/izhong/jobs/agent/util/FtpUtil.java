package me.izhong.jobs.agent.util;

import com.jcraft.jsch.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import me.izhong.jobs.agent.util.bean.FileInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FtpUtil {
	/**
	 * 发送文件到ftp服务器
	 * 
	 * @param host 主机名，格式为<ftp|sftp>://hostname:port
	 * @param user 用户名
	 * @param pass 密码
	 * @param ftpDir 文件上传目录 如果不存在会自动创建
	 * @param ftpFile 文件名
	 * @param srcFile 源文件
	 * @throws Exception
	 */
	public static void putFileToFtp(String host, String user, String pass,
			String ftpDir, String ftpFile, File srcFile) throws Exception {
		checkAddress(host,user,pass);
		if(srcFile == null || !srcFile.exists())
			throw new Exception("srcFile为空,或者不存在");
		String srcName = srcFile.getName();
		String sep = (StringUtils.isBlank(ftpDir)||ftpDir.endsWith("/")) ? "" : "/";
		log.info("上传文件 {} 到 {}{}{}",srcName,ftpDir,sep,ftpFile);
		if(srcFile == null || !srcFile.exists())
			throw new Exception("srcFile为空,或者不存在");
		if (host.startsWith("sftp")) {
			putSftp(host, user, pass, ftpDir, ftpFile, srcFile);
		} else if (host.startsWith("ftp")) {
			putFtp(host, user, pass, ftpDir, ftpFile, srcFile);
		}
	}

	public static void getFileFromFtp(String host, String user, String pass,
			String ftpDir, String ftpFile, File destFile) throws Exception {
		checkAddress(host,user,pass);
		if(destFile == null || !destFile.exists())
			throw new Exception("destFile为空,或者不存在");
		if (host.startsWith("sftp")) {
			getSftp(host, user, pass, ftpDir, ftpFile, destFile);
		} else if (host.startsWith("ftp")) {
			getFtp(host, user, pass, ftpDir, ftpFile, destFile);
		}
	}

	public static FileInfo getFileInfoFromFtp(String host, String user, String pass,
											  String ftpDir, String ftpFile) throws Exception{
		checkAddress(host,user,pass);
		if (host.startsWith("sftp")) {
			return getSftpFileInfo(host, user, pass, ftpDir, ftpFile);
		} else if (host.startsWith("ftp")) {
			return getFtpFileInfo(host, user, pass, ftpDir, ftpFile);
		}
		return null;
	}

	public static List<String> listFtpFilesInDir(String host, String user,
			String pass, String ftpDir) throws Exception {
		checkAddress(host,user,pass);
		if (host.startsWith("sftp")) {
			return listSftpFiles(host, user, pass, ftpDir);
		} else if (host.startsWith("ftp")) {
			return listFtpFiles(host, user, pass, ftpDir);
		} else {
			throw new Exception("FTP地址异常");
		}
	}

	public static void deleteFileFromFtp(String host, String user, String pass,
									String ftpDir, String ftpFile) throws Exception {
		if(StringUtils.isBlank(ftpFile))
			throw new Exception("ftpFile不能为空");
		checkAddress(host,user,pass);
		if (host.startsWith("sftp")) {
			deleteSftp(host, user, pass, ftpDir, ftpFile);
		} else if (host.startsWith("ftp")) {
			deleteFtp(host, user, pass, ftpDir, ftpFile);
		}
	}

	private static void checkAddress(String host,String user, String pass) throws Exception {
		if(StringUtils.isBlank(host) || (!host.startsWith("ftp://") && !host.startsWith("sftp://")) ) {
			throw new Exception("FTP主机地址配置不正确，正确格式为 ftp://x.x.x.x:port 或者 sftp://x.x.x.x:port 实际地址:" + host);
		}
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");
		if(s2 == null || s2.length != 2) {
			throw new Exception("FTP主机地址配置异常，正确格式为 ftp://x.x.x.x:port 实际地址:" + host);
		}
		int port = 0;
		try {
			port = Integer.parseInt(s2[1]);
		} catch (Exception e) {
			throw new Exception("FTP主机地址配置异常，端口号必须是数字，实际数据为:" + s2[1]);
		}
		if(port < 1 || port > 65535)
			throw new Exception("FTP主机地址配置异常，端口号必须在1到65535");
		if(StringUtils.isBlank(user))
			throw new Exception("FTP用户名不能为空");
		if(StringUtils.isBlank(pass))
			throw new Exception("FTP密码不能为空");
	}

	private static void getFtp(String host, String user, String pass,
			String ftpDir, String ftpFile, File destFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		FTPClient ftp = new FTPClient();
		ftp.connect(s2[0], Integer.parseInt(s2[1]));

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("FTP server refused connection.");
		}

		log.info("login");
		if (!ftp.login(user, pass)) {
			throw new Exception("FTP login fail.");
		}

		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.setBufferSize(1024);
		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd");
			ftp.changeWorkingDirectory(ftpDir);
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		@Cleanup
		FileOutputStream fos = new FileOutputStream(destFile);
		log.info("get");
		boolean result = ftp.retrieveFile(ftpFile, fos);

		log.info("disconnect");
		ftp.disconnect();

		if (result == false)
			throw new Exception("Cannot get remote file");
	}

	private static void getSftp(String host, String user, String pass,
			String ftpDir, String ftpFile, File destFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();

		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;

		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd");
			c.cd(ftpDir);
		}
		@Cleanup
		FileOutputStream fos = new FileOutputStream(destFile);
		log.info("get");
		c.get(ftpFile, fos);

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
	}


	private static FileInfo getSftpFileInfo(String host, String user, String pass, String ftpDir, String ftpFile)  throws Exception{
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();

		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;

		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd");
			c.cd(ftpDir);
		}
		log.info("get");
		SftpATTRS sts = c.stat(ftpFile);
		if(sts == null)
			return null;
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(ftpFile);
		Date var1 = new Date((long)sts.getMTime() * 1000L);
		fileInfo.setModifyTime(var1);
		fileInfo.setSize(sts.getSize());

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
		return fileInfo;
	}

	private static FileInfo getFtpFileInfo(String host, String user, String pass, String ftpDir, String ftpFile)  throws Exception{
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		FTPClient ftp = new FTPClient();
		ftp.connect(s2[0], Integer.parseInt(s2[1]));

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("FTP server refused connection.");
		}

		log.info("login");
		if (!ftp.login(user, pass)) {
			throw new Exception("FTP login fail.");
		}

		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.setBufferSize(1024);
		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd");
			ftp.changeWorkingDirectory(ftpDir);
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("get");
		FTPFile[] ftpFiles = ftp.listFiles(ftpFile);
		if(ftpFiles == null || ftpFiles.length  == 0)
			return null;
		FTPFile ff = ftpFiles[0];

		Calendar calendar = ff.getTimestamp();
		int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(ftpFile);
		String modifyString = ftp.getModificationTime(ftpFile);
		if(StringUtils.isBlank(modifyString))
			return null;
		String dateStr = modifyString.split(" ")[1];
		Date date2 = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr);
		calendar.set(Calendar.SECOND,date2.getSeconds());
		Date date = DateUtils.addMilliseconds(calendar.getTime(),zoneOffset);
		fileInfo.setModifyTime(date);
		fileInfo.setSize(ff.getSize());

		log.info("disconnect");
		ftp.disconnect();
		return fileInfo;

	}

	private static void putSftp(String host, String user, String pass,
			String destDir, String destFile, File srcFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect(3000);

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;

		try{
			if(StringUtils.isNotBlank(destDir)) {
				log.info("mkdir");
				String[] dirs = destDir.split("/");
				for (String s : dirs) {
					boolean exist = true;
					try {
						c.lstat(s);
					} catch (Exception ee) {
						exist = false;
					}
					if (!exist) {
						c.mkdir(s.trim());
					}
					c.cd(s.trim());
				}
			}
		} catch (Exception e) {
			log.info("创建文件目录异常:" + destDir,e);
			throw new Exception("创建文件目录异常:" + destDir);
		}

		@Cleanup
		FileInputStream fis = new FileInputStream(srcFile);
		@Cleanup
		BufferedInputStream bfi = new BufferedInputStream(fis);
		log.info("put file {} to {}", destFile, c.pwd());
		c.put(bfi, destFile);

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
	}

	private static void putFtp(String host, String user, String pass,
			String destDir, String destFile, File srcFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		FTPClient ftp = new FTPClient();
		ftp.connect(s2[0], Integer.parseInt(s2[1]));

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("FTP server refused connection.");
		}

		log.info("login");
		if (!ftp.login(user, pass)) {
			throw new Exception("FTP login fail.");
		}

		ftp.setBufferSize(1024);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);

		log.info("cd");
		if (!ftp.changeWorkingDirectory(destDir)) {
			log.info("mkdir");
			if(destDir.length() > 0 && destDir.indexOf("/") > 0){
				String[] temp = destDir.split("/");
				for(String dd : temp){
					if(ftp.changeWorkingDirectory(destDir)) {
						break;
					} else {
						if(!ftp.changeWorkingDirectory(dd)) {
							log.info("mkdir:{}",dd);
							ftp.makeDirectory(dd);
							if (!ftp.changeWorkingDirectory(dd)) {
								throw new Exception("FTP mkdir fail. des:" + destDir);
							}
						}
					}
				}
			}
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		@Cleanup
		FileInputStream fis = new FileInputStream(srcFile);
		@Cleanup
		BufferedInputStream bfi = new BufferedInputStream(fis);
		log.info("put file {} to {}", destFile, destDir);
		ftp.storeFile(destFile, bfi);

		log.info("disconnect");
		ftp.disconnect();
	}

	private static List<String> listFtpFiles(String host, String user,
			String pass, String destDir) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		FTPClient ftp = new FTPClient();
		ftp.connect(s2[0], Integer.parseInt(s2[1]));

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("FTP server refused connection.");
		}

		log.info("login");
		if (!ftp.login(user, pass)) {
			throw new Exception("FTP login fail.");
		}

		ftp.setFileType(FTP.BINARY_FILE_TYPE);

		log.info("cd");
		ftp.changeWorkingDirectory(destDir);

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("ls");
		String[] names = ftp.listNames();
		List<String> list = new ArrayList<String>();
		if(names != null)
			for (String name : names) {
				if (name != null) {
					list.add(name);
				}
			}

		log.info("disconnect");
		ftp.disconnect();

		return list;
	}

	private static List<String> listSftpFiles(String host, String user,
			String pass, String destDir) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshConfig.put("userauth.gssapi-with-mic","no");
		session.setConfig(sshConfig);
		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect(15000);

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;

		log.info("ls");
		Vector v = c.ls(destDir);
		List<String> list = new ArrayList<String>(v.size());
		for (Object o : v) {
			String s = o.toString();
			String[] parts = s.split(" ");
			list.add(parts[parts.length - 1]);
		}

		log.info("disconnect");
		c.disconnect();
		session.disconnect();

		return list;
	}

	private static void deleteFtp(String host, String user, String pass,
							   String ftpDir, String ftpFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		FTPClient ftp = new FTPClient();
		ftp.connect(s2[0], Integer.parseInt(s2[1]));

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new Exception("FTP server refused connection.");
		}

		log.info("login");
		if (!ftp.login(user, pass)) {
			throw new Exception("FTP login fail.");
		}

		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd:{}", ftpDir);
			ftp.changeWorkingDirectory(ftpDir);
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("delete:{}",ftpFile);
		boolean result = ftp.deleteFile(ftpFile);

		log.info("disconnect");
		ftp.disconnect();

		if (result == false)
			throw new Exception("Cannot get remote file");
	}

	private static void deleteSftp(String host, String user, String pass,
								String ftpDir, String ftpFile) throws Exception {
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();

		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;

		if(StringUtils.isNotBlank(ftpDir)) {
			log.info("cd:{}", ftpDir);
			c.cd(ftpDir);
		}
		log.info("delete :{}",ftpFile);
		c.rm(ftpFile);

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
	}

	static public void main(String args[]) throws Exception {
		List<String> files = listFtpFilesInDir("ftp://mirrors.kernel.org:21",
				"anonymous", "adf", "centos");
		for (String file : files) {
			System.out.println(file);
		}
	}

}
