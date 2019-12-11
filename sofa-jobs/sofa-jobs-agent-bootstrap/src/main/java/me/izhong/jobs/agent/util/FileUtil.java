package me.izhong.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileUtil {

    public static void writeStreamToFile(InputStream in, File target) throws Exception{
        BufferedOutputStream out = null;
        BufferedInputStream bin = null;
        long startTime = System.currentTimeMillis();
        try {
            bin = new BufferedInputStream(in);
            out = new BufferedOutputStream(new FileOutputStream(target));
            int bufSize = 2048;
            byte[] buf = new byte[bufSize];
            int readSize = -1;
            while ((readSize = bin.read(buf)) != -1) {
                out.write(buf, 0, readSize);
            }
        } catch (Exception e) {
            String msg = "解压文件失败 :" +target.getName()+ "-"+e.getMessage();
            throw new Exception(msg, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    String msg = "关闭输出流失败:" + e.getMessage();
                    log.error(msg, e);
                }
            }
            long end = System.currentTimeMillis();
            log.info("写入文件 {} 到zip耗时 {} s", target.getAbsolutePath(), (end-startTime)/1000);
        }
    }

    /**
     * 解压文件
     * @param zipFilePath 压缩文件全目录，带文件名
     * @param unzipFilePath 解压后文件全目录，带文件名
     */
    public static boolean unZipFile(String zipFilePath,String unzipFilePath){
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;
        File tmp_unzip_file = null;
        try{
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            zipEntry = zipIn.getNextEntry();
            tmp_unzip_file=new File(unzipFilePath);
            writeStreamToFile(zipIn, tmp_unzip_file);
            return true;
        }catch(Exception e){
            log.error("解压文件出错",e);
            return false;
        }finally{
            if (zipIn != null) {
                try {
                    zipIn.close();
                } catch (Exception e) {
                    log.error("",e);
                }
            }
        }
    }

    public static void deleteFiles(File[] files)
    {
        for(File file : files)
        {
            file.delete();
        }
    }
}
