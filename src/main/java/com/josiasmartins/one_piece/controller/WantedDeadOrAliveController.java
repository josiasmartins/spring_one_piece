package com.josiasmartins.one_piece.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/wanted_by_navy")
public class WantedDeadOrAliveController {

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Carregar a imagem padrão
            BufferedImage backgroundImage = ImageIO.read(new ClassPathResource("static/imagem_procurado_onepiece.jpg").getInputStream());

            // Lê a imagem enviada
            BufferedImage userImage = ImageIO.read(file.getInputStream());

            // Sobrepor a imagem do usuário sobre a imagem padrão
            BufferedImage combinedImage = overlayImage(backgroundImage, userImage);

            // Converte a imagem combinada de volta para um array de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity.ok().body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private BufferedImage overlayImage(BufferedImage backgroundImage, BufferedImage userImage) {
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        // Cria uma nova imagem combinada
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) combinedImage.getGraphics();

        // Desenha a imagem padrão
        g2d.drawImage(backgroundImage, 0, 0, null);

        // Calcula as coordenadas para centralizar a imagem do usuário
        int x = (width - userImage.getWidth()) / 2;
        int y = (height - userImage.getHeight()) / 2;

        // Desenha a imagem do usuário sobre a imagem padrão
        g2d.drawImage(userImage, x, y, null);

        g2d.dispose();

        return combinedImage;
    }

}
