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
	 * @param host
	 *            主机名，格式为<ftp|sftp>://hostname:port
	 * @param user
	 *            用户名
	 * @param pass
	 *            密码
	 * @param destDir
	 *            文件上传目录
	 * @param destFile
	 *            文件名
	 * @param srcFile
	 *            源文件
	 * @throws Exception
	 */
	public static void putFileToFtp(String host, String user, String pass,
			String destDir, String destFile, File srcFile) throws Exception {
		if (host.startsWith("sftp")) {
			putSftp(host, user, pass, destDir, destFile, srcFile);
		} else if (host.startsWith("ftp")) {
			putFtp(host, user, pass, destDir, destFile, srcFile);
		}
	}

	public static boolean getFileFromFtp(String host, String user, String pass,
			String srcDir, String srcFile, File destFile)
	{
		try
		{
			if (host.startsWith("sftp"))
			{
				getSftp(host, user, pass, srcDir, srcFile, destFile);
			} else if (host.startsWith("ftp"))
			{
				getFtp(host, user, pass, srcDir, srcFile, destFile);
			}
		}
		catch (Exception e)
		{
			log.error("下载失败",e);
			return false;
		}
		return true;
	}

	public static FileInfo getFileInfoFromFtp(String host, String user, String pass,
											  String srcDir, String srcFile) throws Exception{
		if (host.startsWith("sftp")) {
			return getSftpFileInfo(host, user, pass, srcDir, srcFile);
		} else if (host.startsWith("ftp")) {
			return getFtpFileInfo(host, user, pass, srcDir, srcFile);
		}
		throw new Exception("地址异常");
	}

	public static List<String> listFtpFilesInDir(String host, String user,
			String pass, String destDir) throws Exception {
		if (host.startsWith("sftp")) {
			return listSftpFiles(host, user, pass, destDir);
		} else if (host.startsWith("ftp")) {
			return listFtpFiles(host, user, pass, destDir);
		} else {
			return new ArrayList<String>();
		}
	}

	public static void deleteFileFromFtp(String host, String user, String pass,
									String destDir, String destFile) throws Exception {
		if (host.startsWith("sftp")) {
			deleteSftp(host, user, pass, destDir, destFile);
		} else if (host.startsWith("ftp")) {
			deleteFtp(host, user, pass, destDir, destFile);
		}
	}

	private static void getFtp(String host, String user, String pass,
			String srcDir, String srcFile, File destFile) throws Exception {
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
		log.info("cd");
		ftp.changeWorkingDirectory(srcDir);

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		@Cleanup
		FileOutputStream fos = new FileOutputStream(destFile);
		log.info("get");
		boolean result = ftp.retrieveFile(srcFile, fos);

		log.info("disconnect");
		ftp.disconnect();

		if (result == false)
			throw new Exception("Cannot get remote file");
	}

	private static void getSftp(String host, String user, String pass,
			String srcDir, String srcFile, File destFile) throws Exception {
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

		log.info("cd");
		c.cd(srcDir);
		@Cleanup
		FileOutputStream fos = new FileOutputStream(destFile);
		log.info("get");
		c.get(srcFile, fos);

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
	}


	private static FileInfo getSftpFileInfo(String host, String user, String pass, String srcDir, String srcFile)  throws Exception{
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

		log.info("cd");
		c.cd(srcDir);
		log.info("get");
		SftpATTRS sts = c.stat(srcFile);
		if(sts == null)
			return null;
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(srcFile);
		Date var1 = new Date((long)sts.getMTime() * 1000L);
		fileInfo.setModifyTime(var1);
		fileInfo.setSize(sts.getSize());

		log.info("disconnect");
		c.disconnect();
		session.disconnect();
		return fileInfo;
	}

	private static FileInfo getFtpFileInfo(String host, String user, String pass, String srcDir, String srcFile)  throws Exception{
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
		log.info("cd");
		ftp.changeWorkingDirectory(srcDir);

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("get");
		FTPFile[] ftpFiles = ftp.listFiles(srcFile);
		if(ftpFiles == null || ftpFiles.length  == 0)
			return null;
		FTPFile ftpFile = ftpFiles[0];

		Calendar calendar = ftpFile.getTimestamp();
		int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(srcFile);
		String modifyString = ftp.getModificationTime(srcFile);
		if(StringUtils.isBlank(modifyString))
			return null;
		String dateStr = modifyString.split(" ")[1];
		Date date2 = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr);
		calendar.set(Calendar.SECOND,date2.getSeconds());
		Date date = DateUtils.addMilliseconds(calendar.getTime(),zoneOffset);
		fileInfo.setModifyTime(date);
		fileInfo.setSize(ftpFile.getSize());

		log.info("disconnect");
		ftp.disconnect();
		return fileInfo;

	}

	private static void putSftp(String host, String user, String pass,
			String destDir, String destFile, File srcFile) throws Exception {
		log.info("u:{},p:{}",user,pass);
		String s1[] = host.split("://");
		String s2[] = s1[1].split(":");

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, s2[0], Integer.parseInt(s2[1]));

		session.setPassword(pass);
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);

		log.info("connect {}:{}", s2[0], Integer.parseInt(s2[1]));
		session.connect(1500);

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
		log.info("ftp:{},user:{},p:{}",host,user,pass);
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
							   String destDir, String destFile) throws Exception {
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
		log.info("cd:{}",destDir);
		ftp.changeWorkingDirectory(destDir);

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("delete:{}",destFile);
		boolean result = ftp.deleteFile(destFile);

		log.info("disconnect");
		ftp.disconnect();

		if (result == false)
			throw new Exception("Cannot get remote file");
	}

	private static void deleteSftp(String host, String user, String pass,
								String destDir, String destFile) throws Exception {
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

		log.info("cd:{}",destDir);
		c.cd(destDir);
		log.info("delete :{}",destFile);
		c.rm(destFile);

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
