package com.risksmart.common.core.utils.file;

import java.io.File;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件类型工具类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class FileTypeUtils
{
    private FileTypeUtils()
    {
    }

    /**
     * 获取文件类型
     * <p>
     * 例如: ruoyi.txt, 返回: txt
     * 
     * @param file 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(File file)
    {
        if (null == file)
        {
            return StringUtils.EMPTY;
        }
        return getFileType(file.getName());
    }

    /**
     * 获取文件类型
     * <p>
     * 例如: ruoyi.txt, 返回: txt
     *
     * @param fileName 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return StringUtils.EMPTY;
        }
        int separatorIndex = fileName.lastIndexOf(".");
        if (separatorIndex < 0)
        {
            return StringUtils.EMPTY;
        }
        return fileName.substring(separatorIndex + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 获取文件名的后缀
     * 
     * @param file 表单文件
     * @return 后缀名
     */
    public static String getExtension(MultipartFile file)
    {
        if (file == null)
        {
            return StringUtils.EMPTY;
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension))
        {
            String contentType = file.getContentType();
            if (StringUtils.isEmpty(contentType))
            {
                return StringUtils.EMPTY;
            }
            extension = MimeTypeUtils.getExtension(contentType);
        }
        return extension;
    }

    /**
     * 获取文件类型
     * 
     * @param photoByte 文件字节码
     * @return 后缀（不含".")
     */
    public static String getFileExtendName(byte[] photoByte)
    {
        if (photoByte == null || photoByte.length < 10)
        {
            return StringUtils.EMPTY;
        }
        String strFileExtendName = "JPG";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97))
        {
            strFileExtendName = "GIF";
        }
        else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70))
        {
            strFileExtendName = "JPG";
        }
        else if ((photoByte[0] == 66) && (photoByte[1] == 77))
        {
            strFileExtendName = "BMP";
        }
        else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71))
        {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }
}
