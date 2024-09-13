package com.josiasmartins.one_piece.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/wanted_by_navy")
public class WantedDeadOrAliveController {

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "width", defaultValue = "417") int width,
            @RequestParam(value = "height", defaultValue = "310") int height,
            @RequestParam(value = "verticalOffset", defaultValue = "-55") int verticalOffset,
            @RequestParam(value = "name", defaultValue = "Ninja Copiador") String name,
            @RequestParam(value = "reward", defaultValue = "300,00") String reward,
            @RequestParam(value = "nameX", defaultValue = "50") int nameX,
            @RequestParam(value = "nameY", defaultValue = "600") int nameY,
            @RequestParam(value = "rewardX", defaultValue = "87") int rewardX,
            @RequestParam(value = "rewardY", defaultValue = "673") int rewardY,
            @RequestParam(value = "nameFontSize", defaultValue = "55") float nameFontSize,
            @RequestParam(value = "rewardFontSize", defaultValue = "40") float rewardFontSize) {
        try {
            // Carregar a imagem padrão
            BufferedImage backgroundImage = ImageIO.read(new ClassPathResource("static/imagem_procurado_onepiece.jpg").getInputStream());

            // Lê a imagem enviada
            BufferedImage userImage = ImageIO.read(file.getInputStream());

            // Redimensionar a imagem do usuário
            BufferedImage resizedUserImage = resizeImage(userImage, width, height);

            // Sobrepor a imagem do usuário sobre a imagem padrão com deslocamento vertical
            BufferedImage combinedImage = overlayImage(backgroundImage, resizedUserImage, verticalOffset, name, reward, nameX, nameY, rewardX, rewardY, nameFontSize, rewardFontSize);

            // Converte a imagem combinada de volta para um array de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Configura o cabeçalho da resposta para tipo de imagem
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    private BufferedImage overlayImage(BufferedImage backgroundImage, BufferedImage userImage, int verticalOffset, String name, String reward, int nameX, int nameY, int rewardX, int rewardY, float nameFontSize, float rewardFontSize) {
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        // Cria uma nova imagem combinada
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) combinedImage.getGraphics();

        // Desenha a imagem padrão
        g2d.drawImage(backgroundImage, 0, 0, null);

        // Calcula as coordenadas para centralizar a imagem do usuário com um deslocamento vertical
        int x = (width - userImage.getWidth()) / 2;
        int y = (height - userImage.getHeight()) / 2 + verticalOffset;

        // Desenha a imagem do usuário sobre a imagem padrão
        g2d.drawImage(userImage, x, y, null);

        // Adiciona o nome e o valor da recompensa na parte inferior da imagem com posições ajustáveis
        addTextToImage(g2d, name, reward, nameX, nameY, rewardX, rewardY, nameFontSize, rewardFontSize);

        g2d.dispose();

        return combinedImage;
    }

    private void addTextToImage(Graphics2D g2d, String name, String reward, int nameX, int nameY, int rewardX, int rewardY, float nameFontSize, float rewardFontSize) {
        try {
            // Carregar a fonte personalizada para o nome
            InputStream fontStream = new ClassPathResource("static/fonts/CenturyOldStyleStd-Bold.otf").getInputStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            Font nameFont = font.deriveFont(nameFontSize);
            g2d.setFont(nameFont);
        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de falha ao carregar a fonte, usa uma fonte padrão
            g2d.setFont(new Font("Arial", Font.BOLD, (int) nameFontSize));
        }

        // Define a cor do texto
        g2d.setColor(Color.WHITE);

        // Adiciona o texto do nome
        g2d.drawString(name, nameX, nameY);

        try {
            // Recarregar a fonte personalizada para a recompensa
            InputStream fontStream = new ClassPathResource("static/fonts/CenturyOldStyleStd-Bold.otf").getInputStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            Font rewardFont = font.deriveFont(rewardFontSize);
            g2d.setFont(rewardFont);
        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de falha ao carregar a fonte, usa uma fonte padrão
            g2d.setFont(new Font("Arial", Font.BOLD, (int) rewardFontSize));
        }

        // Adiciona o texto da recompensa
        g2d.drawString(reward, rewardX, rewardY);
    }

}
