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

	private static String FTP_DEFAULT_CHARSET = "UTF-8";
	private static String FTP_SERVER_CHARSET = "ISO-8859-1";

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
		String charset = getFtpEncode(ftp);
		if(StringUtils.isNotBlank(ftpDir)) {
			boolean rt = ftp.changeWorkingDirectory(toFtpEncode(ftpDir,charset));
			if(rt) {
				log.info("cd {} success",ftpDir);
			} else {
				log.info("cd {} fail",ftpDir);
			}
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

		try {
			if (StringUtils.isNotBlank(ftpDir)) {
				log.info("cd");
				c.cd(ftpDir);
			}
			@Cleanup
			FileOutputStream fos = new FileOutputStream(destFile);
			log.info("get");
			c.get(ftpFile, fos);
		} finally {
			log.info("disconnect");
			c.disconnect();
			session.disconnect();
		}
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

		SftpATTRS sts;
		try {
			if (StringUtils.isNotBlank(ftpDir)) {
				log.info("cd {}", ftpDir);
				c.cd(ftpDir);
			}
			log.info("get");
			sts = c.stat(ftpFile);
		} finally {
			log.info("disconnect");
			c.disconnect();
			session.disconnect();
		}
		if (sts == null)
			return null;

		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(ftpFile);
		Date var1 = new Date((long)sts.getMTime() * 1000L);
		fileInfo.setModifyTime(var1);
		fileInfo.setSize(sts.getSize());
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
		String charset = getFtpEncode(ftp);
		if(StringUtils.isNotBlank(ftpDir)) {
			boolean rt = ftp.changeWorkingDirectory(toFtpEncode(ftpDir,charset));
			if(rt) {
				log.info("cd {} success",ftpDir);
			} else {
				log.info("cd {} fail",ftpDir);
			}
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("get");
		FTPFile[] ftpFiles = ftp.listFiles(toFtpEncode(ftpFile,charset));
		if(ftpFiles == null || ftpFiles.length  == 0) {
			log.info("文件 {} 不存在",ftpFile);
			return null;
		}
		FTPFile ff = ftpFiles[0];

		Calendar calendar = ff.getTimestamp();
		int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(ftpFile);
		String modifyString = ftp.getModificationTime(toFtpEncode(ftpFile,charset));
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

		try {
			try {
				if (StringUtils.isNotBlank(destDir)) {
					log.info("mkdir");
					if (destDir.startsWith("/")) {
						c.cd("/");
						destDir = destDir.substring(1);
					}
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
				log.info("创建文件目录异常:" + destDir, e);
				throw new Exception("创建文件目录异常:" + destDir);
			}

			@Cleanup
			FileInputStream fis = new FileInputStream(srcFile);
			@Cleanup
			BufferedInputStream bfi = new BufferedInputStream(fis);
			log.info("put file {} to {}", destFile, c.pwd());
			c.put(bfi, destFile);
		} finally {
			log.info("disconnect");
			c.disconnect();
			session.disconnect();
		}
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

		String charset = getFtpEncode(ftp);
		ftp.setControlEncoding(charset);
		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("cd");
		if (!ftp.changeWorkingDirectory(destDir)) {
			log.info("mkdir");
			if(destDir.startsWith("/")) {
				ftp.changeWorkingDirectory("/");
				destDir = destDir.substring(1);
			}
			if(destDir.length() > 0 && destDir.indexOf("/") > 0){
				String[] temp = destDir.split("/");
				for(String dd : temp){
					if(ftp.changeWorkingDirectory(toFtpEncode(destDir,charset))) {
						break;
					} else {
						if(!ftp.changeWorkingDirectory(toFtpEncode(dd,charset))) {
							log.info("mkdir:{}",dd);
							ftp.makeDirectory(toFtpEncode(dd,charset));
							if (!ftp.changeWorkingDirectory(toFtpEncode(dd,charset))) {
								throw new Exception("FTP mkdir fail. des:" + destDir);
							}
						}
					}
				}
			}
		}

		@Cleanup
		FileInputStream fis = new FileInputStream(srcFile);
		@Cleanup
		BufferedInputStream bfi = new BufferedInputStream(fis);
		log.info("put file {} to {}", destFile, fromFtpEncode(ftp.printWorkingDirectory(),charset));
		ftp.storeFile(toFtpEncode(destFile,charset), bfi);

		log.info("disconnect");
		ftp.disconnect();
	}

	private static List<String> listFtpFiles(String host, String user,
			String pass, String ftpDir) throws Exception {
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
		String charset = getFtpEncode(ftp);
		boolean rt = ftp.changeWorkingDirectory(toFtpEncode(ftpDir,charset));
		if(rt) {
			log.info("cd {} success",ftpDir);
		} else {
			log.info("cd {} fail",ftpDir);
		}
		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("ls");
		String[] names = ftp.listNames();
		List<String> list = new ArrayList<String>();
		if(names != null)
			for (String name : names) {
				if (name != null) {
					list.add(fromFtpEncode(name,charset));
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

		List<String> list;
		try {
			log.info("ls");
			Vector v = c.ls(destDir);
			list = new ArrayList<String>(v.size());
			for (Object o : v) {
				String s = o.toString();
				String[] parts = s.split(" ");
				list.add(parts[parts.length - 1]);
			}
		} finally {
			log.info("disconnect");
			c.disconnect();
			session.disconnect();
		}
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

		String charset = getFtpEncode(ftp);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		if(StringUtils.isNotBlank(ftpDir)) {
			boolean rt = ftp.changeWorkingDirectory(toFtpEncode(ftpDir,charset));
			if(rt) {
				log.info("cd {} success",ftpDir);
			} else {
				log.info("cd {} fail",ftpDir);
			}
		}

		// 设置被动模式
		ftp.enterLocalPassiveMode();

		log.info("delete:{}",ftpFile);
		boolean result = ftp.deleteFile(toFtpEncode(ftpFile,charset));
		if(!result) {
			result = ftp.removeDirectory(toFtpEncode(ftpFile,charset));
		}

		log.info("disconnect");
		ftp.disconnect();

		if (result == false)
			throw new Exception("Cannot delete remote file");
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
			log.info("cd {}", ftpDir);
			c.cd(ftpDir);
		}
		log.info("delete {}", ftpFile);
		try {
			try {
				c.rm(ftpFile);
			} catch (Exception e) {
				c.rmdir(ftpFile);
			}
		} catch (Exception a) {
			throw new Exception("删除文件失败:" + ftpFile);
		} finally {
			log.info("disconnect");
			c.disconnect();
			session.disconnect();
		}
	}

	static private String getFtpEncode(FTPClient ftp) throws Exception{
		String charset = FTP_DEFAULT_CHARSET;
		if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {
			charset = "UTF-8";
		} else if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS GBK", "ON"))) {
			charset = "GBK";
		}
		return charset;
	}
	static private String toFtpEncode(String data, String ftpEncode) throws Exception{
		return new String(data.getBytes(ftpEncode),FTP_SERVER_CHARSET);
	}
	static private String fromFtpEncode(String data, String ftpEncode) throws Exception{
		return new String(data.getBytes(FTP_SERVER_CHARSET),ftpEncode);
	}
}
