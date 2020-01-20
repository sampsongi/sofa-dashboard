package me.izhong.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtil {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    /**
     * 压缩文件，文件夹
     * @param zipFileName 压缩文件后的zip文件名称，全路径
     * @param inputFile 要压缩的文件，文件夹
     * @throws Exception
     */
    public static void zip(String zipFileName, File inputFile) throws Exception {
        zip(zipFileName,inputFile,DEFAULT_CHARSET);
    }

    public static void zip(String zipFileName, File inputFile,Charset charset) throws Exception {
        if(StringUtils.isBlank(zipFileName))
            throw new Exception("要压缩的zip文件名称不能为空");
        if (inputFile == null || !inputFile.exists())
            throw new Exception("要压缩的文件不存在");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName), charset);
        try {
            zip(out, inputFile, "");
        } finally {
            out.close();
        }
    }

    /**
     * 压缩文件 压缩后的文件在同一个目录
     * @param zipFileName 压缩文件后的zip文件名称，全路径
     * @param inputFiles 要压缩的文件列表
     * @throws Exception
     */
    public static void zipFiles(String zipFileName, List<File> inputFiles) throws Exception {
        zipFiles(zipFileName,inputFiles,DEFAULT_CHARSET);
    }

    public static void zipFiles(String zipFileName, List<File> inputFiles,Charset charset) throws Exception {
        if (inputFiles == null || inputFiles.size() == 0)
            throw new Exception("要压缩的文件不存在");
        for (File f : inputFiles) {
            if (!f.exists()) {
                throw new Exception("文件" + f.getName() + "不存在");
            }
        }
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName),charset);
        try {
            for (File f : inputFiles) {
                zip(out, f, f.getName());
            }
        } finally {
            out.close();
        }
    }

    public static void zipFiles(String zipFileName, String inputDir, List<String> inputFileNames) throws Exception {
        zipFiles(zipFileName,inputDir,inputFileNames,DEFAULT_CHARSET);
    }

    public static void zipFiles(String zipFileName, String inputDir, List<String> inputFileNames, Charset charset) throws Exception {
        if (inputFileNames == null || inputFileNames.size() == 0)
            throw new Exception("要压缩的文件不存在");
        if (inputDir != null && !inputDir.endsWith("/"))
            inputDir += "/";
        List<File> files = new ArrayList<>();
        for(String fileName : inputFileNames){
            files.add(new File(inputDir + fileName));
        }
        zipFiles(zipFileName,files,charset);
    }

    /**
     * 压缩文件
     * @param zipFileName 压缩文件后的zip文件名称，全路径
     * @param inputFile 要压缩的文件
     * @param fileName 压缩文件里面的文件名称
     * @throws Exception
     */
    public static void zipFile(String zipFileName, File inputFile, String fileName) throws Exception {
        zipFile(zipFileName,inputFile,fileName,DEFAULT_CHARSET);
    }

    public static void zipFile(String zipFileName, File inputFile, String fileName, Charset charset) throws Exception {
        if (inputFile == null || !inputFile.exists())
            throw new Exception("要压缩的文件不存在");
        if(StringUtils.isBlank(fileName))
            throw new Exception("要压缩的文件名称不能为空");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName),charset);
        try {
            zip(out, inputFile, fileName);
        } finally {
            out.close();
        }
    }

    private static void zip(ZipOutputStream out, File srcFile, String base) throws Exception {
        if (srcFile.isDirectory()) {
            File[] fl = srcFile.listFiles();
            if(StringUtils.isNotBlank(base))
                out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            if(StringUtils.isBlank(base))
                throw new Exception("压缩异常");
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(srcFile);
            byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
            int b;
            while ((b = in.read(buf)) != -1) {
                out.write(buf, 0, b);
            }
            in.close();
            out.flush();
            out.closeEntry();
        }
    }

    /**
     * 解压文件
     * @param zipFileName zip文件名称，全路径
     * @param path 解压后的文件夹名称
     * @throws Exception
     */
    public static void unzip(String zipFileName, String path) throws Exception {
        unzip(zipFileName,path,DEFAULT_CHARSET);
    }

    public static void unzip(String zipFileName, String path, Charset charset) throws Exception {
        if (zipFileName == null || !new File(zipFileName).exists())
            throw new Exception("要解压的文件不存在");
        if(StringUtils.isBlank(path))
            throw new Exception("要解压的文件路径不能为空");
        if(!path.endsWith(File.separator)) {
            path += File.separator;
        }
        ZipInputStream zi = new ZipInputStream(new FileInputStream(zipFileName),charset);

        ZipEntry ze = zi.getNextEntry();
        while (zi.available() > 0) {
            unzip(zi, ze,path);
            ze = zi.getNextEntry();
        }
        zi.close();
//        使用 ZipEntry 无法解析大文件，ZipInputStream可以
//        ZipFile zf = new ZipFile(new File(zipFileName),charset);
//        Enumeration<? extends ZipEntry> en = zf.entries();
//        while (en.hasMoreElements()) {
//            unzip(zf, en.nextElement(),path);
//        }
    }

    private static void unzip(ZipInputStream zi , ZipEntry zipEntry, String base) throws Exception {
        if (zipEntry.isDirectory()) {
            base = base + File.separator + zipEntry.getName();
            File dir = new File(base);
            if(!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            String filepath = base + File.separator + zipEntry.getName();
            File f = new File(filepath);
            File parentFile = f.getParentFile();
            if(!parentFile.exists())
                parentFile.mkdirs();
            FileOutputStream fos = new FileOutputStream(filepath);
            int len;
            byte buf[] = new byte[DEFAULT_BUFFER_SIZE];
            while (-1 != (len = zi.read(buf))) {
                fos.write(buf, 0, len);
            }
            fos.close();
        }
    }

    public static void main(String[] temp) {
        try {
            String tmpDir = File.createTempFile("zip","tmp").getParent();
            log.info("设置临时工作目录:{}",tmpDir);
            String sourceDir = tmpDir + File.separator + "source";
            new File(sourceDir).mkdir();
            log.info("设置源目录:{}",sourceDir);
            String destDir = tmpDir + File.separator + "dest";
            new File(destDir).mkdir();
            log.info("设置目标目录:{}",destDir);

            File sourceFile1 = new File(sourceDir + File.separator + "测试文件1.txt");
            sourceFile1.createNewFile();
            FileUtils.write(sourceFile1,"content1内容", DEFAULT_CHARSET);
            File sourceFile2 = new File(sourceDir + File.separator + "测试文件2.txt");
            sourceFile2.createNewFile();
            FileUtils.write(sourceFile2,"content2内容", DEFAULT_CHARSET);

            File sourceDir1 = new File(sourceDir + File.separator + "测试目录");
            sourceDir1.mkdirs();
            File sourceDirFile1 = new File(sourceDir1.getAbsolutePath() + File.separator + "测试子文件.txt");
            sourceDirFile1.createNewFile();

            log.info("列出源文件夹文件");
            for(File f: new File(sourceDir).listFiles()){
                if(f.isDirectory()) {
                    log.info("目录:{}" , f.getName());
                    for(File f2: f.listFiles()){
                        log.info("文件:{}/{}",f.getName(),f2.getName());
                    }
                } else {
                    log.info("文件:{}",f.getName());
                }
            }

            //压缩文件
            zip(destDir + File.separator + "压缩后的文件夹.zip",new File(sourceDir));
            log.info("压缩文件成功:{}",destDir + File.separator + "压缩后的文件夹.zip");

            //如果是utf8编码 fileName 不能含有 自定义，否则win自带的解压缩工具显示不了zip里面文件名字
            zipFile(destDir + File.separator + "压缩单个文件.zip",sourceFile1,"测试文件1444自定义.txt");

            zipFiles(destDir + File.separator + "压缩后的文件列表1.zip", Arrays.asList(new File[]{sourceFile1,sourceFile2}));

            zipFiles(destDir + File.separator + "压缩后的文件列表2.zip",sourceDir, Arrays.asList(new String[]{sourceFile1.getName(),sourceFile2.getName()}));

            log.info("压缩成功");
            File unzipDir = new File(destDir +File.separator + "unzip");
            if(!unzipDir.exists())
                unzipDir.mkdir();
            unzip(destDir + File.separator + "压缩后的文件夹.zip",unzipDir.getAbsolutePath());
            log.info("解压成功");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
