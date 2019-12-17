package me.izhong.jobs.agent.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    /**
     * 压缩文件，文件夹
     * @param zipFileName 压缩文件后的zip文件名称，全路径
     * @param inputFile 要压缩的文件，文件夹
     * @throws Exception
     */
    public static void zip(String zipFileName, File inputFile) throws Exception {
        if(StringUtils.isBlank(zipFileName))
            throw new Exception("要压缩的zip文件名称不能为空");
        if (inputFile == null || !inputFile.exists())
            throw new Exception("要压缩的文件不存在");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        out.close();
    }

    /**
     * 压缩文件
     * @param zipFileName 压缩文件后的zip文件名称，全路径
     * @param inputFile 要压缩的文件
     * @param fileName 压缩文件里面的文件名称
     * @throws Exception
     */
    public static void zipFile(String zipFileName, File inputFile, String fileName) throws Exception {
        if (inputFile == null || !inputFile.exists())
            throw new Exception("要压缩的文件不存在");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        out.putNextEntry(new ZipEntry(fileName));
        FileInputStream in = new FileInputStream(inputFile);
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int b;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        in.close();
        out.close();
    }

    private static void zip(ZipOutputStream out, File srcFile, String base) throws Exception {
        if (srcFile.isDirectory()) {
            File[] fl = srcFile.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(srcFile);
            byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
            int b;
            while ((b = in.read(buf)) != -1) {
                out.write(buf, 0, b);
            }
            in.close();
        }
    }

    /**
     * 解压文件
     * @param zipFileName zip文件名称，全路径
     * @param path 解压后的文件夹名称
     * @throws Exception
     */
    public static void unzip(String zipFileName, String path) throws Exception {
        if (zipFileName == null || !new File(zipFileName).exists())
            throw new Exception("要解压的文件不存在");
        if(StringUtils.isBlank(path))
            throw new Exception("要解压的文件路径不能为空");
        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }
        ZipFile zf = new ZipFile(new File(zipFileName));
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            unzip(zf, en.nextElement(),path);
        }
    }

    private static void unzip(ZipFile zf , ZipEntry zipEntry, String base) throws Exception {
        if (zipEntry.isDirectory()) {
            base = base + File.separator + zipEntry.getName();
            File dir = new File(base);
            if(!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            String filepath = base + File.separator + zipEntry.getName();
            FileOutputStream fos = new FileOutputStream(filepath);
            InputStream is = zf.getInputStream(zipEntry);
            File f = new File(filepath);
            File parentFile = f.getParentFile();
            if(!parentFile.exists())
                parentFile.mkdirs();
            int len = 0;
            byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
            while (-1 != (len = is.read(buf))) {
                fos.write(buf, 0, len);
            }
            is.close();
            fos.close();
        }
    }


    public static void main(String[] temp) {
        try {
            //zipFile("D:\\logs\\hello.zip", new File("D:\\logs\\zk.log\\tt.txt"),"xx.tt");
            zip("D:\\logs\\hello.zip", new File("D:\\logs\\zk.log"));
            System.out.println("压缩完成"); // 输出信息
            unzip("D:\\logs\\hello.zip","D:\\logs\\ppp");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
