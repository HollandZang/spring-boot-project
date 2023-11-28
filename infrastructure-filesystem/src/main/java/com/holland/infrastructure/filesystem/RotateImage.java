package com.holland.infrastructure.filesystem;


import cn.hutool.core.io.IoUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片旋转工具
 */
@Slf4j
public class RotateImage {

    /**
     * 根据图片元数据信息旋转图片
     * 如果花费时间过多，需要只判断 IOS 才做
     *
     * @param is        consumed
     * @param extension 文件后缀名
     * @return 旋转后的流
     */
    public static InputStream rotateImage(InputStream is, String extension) {
        byte[] bytes = IoUtil.readBytes(is);

        StringBuilder description = new StringBuilder();
        try (InputStream isTmp = new ByteArrayInputStream(bytes)) {
            Metadata metadata = ImageMetadataReader.readMetadata(isTmp);
            metadata.getDirectories().forEach(directory -> directory.getTags().forEach(tag -> {
                if (tag.getTagType() == ExifDirectoryBase.TAG_ORIENTATION) {
                    description.append(tag.getDescription().replaceAll(" ", ""));
                }
            }));
        } catch (IOException | ImageProcessingException e) {
            log.error("获取图片元数据失败", e);
            return new ByteArrayInputStream(bytes);
        }

        if (description.length() > 0) {
            int rotateIndex = description.indexOf("Rotate");
            int cwIndex = description.indexOf("CW");
            if (rotateIndex >= 0 && cwIndex > 0 && rotateIndex < cwIndex) {
                int angel = Integer.parseInt(description.substring(rotateIndex + 6, cwIndex));
                try (InputStream isTmp = new ByteArrayInputStream(bytes);
                     ByteArrayOutputStream os = new ByteArrayOutputStream();
                ) {
                    BufferedImage oldImage = ImageIO.read(isTmp);
                    BufferedImage newImage = RotateImage.rotate(oldImage, angel);
                    ImageIO.write(newImage, extension, os);
                    newImage.getGraphics().dispose();
                    oldImage.getGraphics().dispose();

                    byte[] newBytes = os.toByteArray();
                    return new ByteArrayInputStream(newBytes);
                } catch (IOException e) {
                    log.error("旋转图片失败", e);
                    return new ByteArrayInputStream(bytes);
                }
            }
        }
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 对图片进行旋转
     *
     * @param src   被旋转图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    private static BufferedImage rotate(Image src, int angel) {
        int srcWidth = src.getWidth(null);
        int srcHeight = src.getHeight(null);
        // 计算旋转后图片的尺寸
        Rectangle rectDes = calcRotatedSize(new Rectangle(new Dimension(
                srcWidth, srcHeight)), angel);
        BufferedImage res = null;
        res = new BufferedImage(rectDes.width, rectDes.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // 进行转换
        g2.translate((rectDes.width - srcWidth) / 2,
                (rectDes.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);

        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 计算旋转后的图片
     *
     * @param src   被旋转的图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    private static Rectangle calcRotatedSize(Rectangle src, int angel) {
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angelDeltaWidth = Math.atan((double) src.height / src.width);
        double angelDeltaHeight = Math.atan((double) src.width / src.height);

        int lenDeltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha - angelDeltaWidth));
        int lenDeltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha - angelDeltaHeight));
        int desWidth = src.width + lenDeltaWidth * 2;
        int desHeight = src.height + lenDeltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }
}