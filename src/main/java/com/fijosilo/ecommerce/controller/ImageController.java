package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.repository.JPAClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    private String resourceFolder;

    private static final Logger log = LoggerFactory.getLogger(JPAClientRepository.class);

    public ImageController(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    @GetMapping("/{fileParameter}")
    public void download(@PathVariable("fileParameter") String fileName, HttpServletResponse response) {
        // deliver the image if it is found else return page not found (404)
        try {
            File file = new File(resourceFolder + "/image/" + fileName);
            response.setStatus(200);
            response.setContentType("image/" + fileName.replaceFirst(".*[.]", ""));
            new FileInputStream(file).transferTo(response.getOutputStream());
        } catch (NullPointerException | SecurityException | IOException e) {
            response.setStatus(404);
        }
    }

    @PostMapping
    public HashMap<String, Object> upload(@RequestParam("image") MultipartFile file) {
        HashMap<String, Object> response = new HashMap<>();

        // give the image a unique name
        String name = UUID.randomUUID().toString();

        // get the image extension
        String extension = file.getContentType().replaceFirst(".*\\/", "");

        // calculate relative and absolute paths
        String relPath = String.format("/image/%s.%s", name, extension);
        String absPath = String.format("%s/%s", resourceFolder, relPath);

        // save the image
        try {
            // save the multipart file to a file
            File f = new File(absPath);
            file.transferTo(f);

            // resize the image if needed
            resizeImageIfExceeds(f, extension, 1920, 1080);

            response.put("error", false);
            response.put("url", relPath);
            return response;
        } catch (NullPointerException | IllegalStateException | IOException e) {
            log.warn(e.getMessage());

            response.put("error", true);
            response.put("message", "Server couldn't save the image.");
            return response;
        }

    }

    /**
     * Resizes an image, keeping the aspect ratio, if any of its dimensions surpass the input dimensions.
     *
     * @param file a File object of the image to be resized
     * @param extension the image file extension
     * @param width the input width if the image width is bigger than this it gets resized
     * @param height the input height if the image height is bigger than this it gets resized
     * @throws IllegalArgumentException if the input file is invalid
     * @throws IOException if it could not read or write to the input file
     */
    private void resizeImageIfExceeds(File file, String extension, int width, int height) throws IllegalArgumentException, IOException {
        // load source buffered image from the file
        BufferedImage source = ImageIO.read(file);
        // check if it needs to be resized
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (sourceWidth > 1920 || sourceHeight > 1080) {
            // calculate the new size but keep aspect ratio
            int targetWidth = sourceWidth;
            int targetHeight = sourceHeight;
            int excessWidth = sourceWidth - 1920;
            int excessHeight = sourceHeight - 1080;
            if (excessWidth > excessHeight) {
                targetWidth = 1920;
                targetHeight = sourceHeight * 1920 / sourceWidth;
            } else {
                targetHeight = 1080;
                targetWidth = sourceWidth * 1080 / sourceHeight;
            }
            // make sure the new size never gets smaller than one pixel
            targetWidth = Math.max(targetWidth, 1);
            targetHeight = Math.max(targetHeight, 1);

            // resize the image
            BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            // draw the source image into the target image
            Graphics2D g = target.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(source, 0, 0, targetWidth, targetHeight, null);
            g.dispose();
            // save the resized image back to the file
            ImageIO.write(target, extension, file);
        }
    }

}
