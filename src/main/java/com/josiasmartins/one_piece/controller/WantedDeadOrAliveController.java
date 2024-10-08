package com.josiasmartins.one_piece.controller;

import com.josiasmartins.one_piece.exceptions.BadRequestException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
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
            @RequestParam(value = "reward", defaultValue = "3,000,000.00") String reward,
            @RequestParam(value = "nameX", defaultValue = "50") int nameX,
            @RequestParam(value = "nameY", defaultValue = "568") int nameY,
            @RequestParam(value = "rewardX", defaultValue = "87") int rewardX,
            @RequestParam(value = "rewardY", defaultValue = "646") int rewardY,
            @RequestParam(value = "nameFontSize", defaultValue = "55") float nameFontSize,
            @RequestParam(value = "rewardFontSize", defaultValue = "40") float rewardFontSize) {
        try {

            name = name.toUpperCase();

            this.validations(name);

            // Carregar a imagem padrão
            BufferedImage backgroundImage = ImageIO.read(new ClassPathResource("static/imagem_procurado_onepiece.jpg").getInputStream());

            // Lê a imagem enviada
            BufferedImage userImage = ImageIO.read(file.getInputStream());

            // Redimensionar a imagem do usuário
            BufferedImage resizedUserImage = resizeImage(userImage, width, height);

            // Sobrepor a imagem do usuário sobre a imagem padrão com deslocamento vertical
            BufferedImage combinedImage = overlayImage(backgroundImage, resizedUserImage, verticalOffset, name, reward, nameX, nameY, rewardX, rewardY, nameFontSize, rewardFontSize);

            // Adiciona efeito de rasgo e envelhecimento
            BufferedImage finalImage = addAgingEffects(combinedImage);

            // Converte a imagem combinada de volta para um array de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "png", baos);
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

    private void addTextToImage(Graphics2D g2d,
                                String name,
                                String reward,
                                int nameX,
                                int nameY,
                                int rewardX,
                                int rewardY,
                                float nameFontSize,
                                float rewardFontSize) {
        try {
            // Carregar a fonte personalizada
            InputStream fontStream = new ClassPathResource("static/fonts/CenturyOldStyleStd-Bold.otf").getInputStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            Font nameFont = font.deriveFont(nameFontSize);
            Font rewardFont = font.deriveFont(rewardFontSize);

            // Adiciona o texto do nome com gradiente
            addGradientText(g2d, name, nameX, nameY, nameFont, new Color(0x4d2605), new Color(0x1b0800));

            // Adiciona o texto da recompensa com gradiente
            addGradientText(g2d, reward, rewardX, rewardY, rewardFont, new Color(0x4d2605), new Color(0x1b0800));

        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de falha ao carregar a fonte, usa uma fonte padrão e uma cor simples
            g2d.setFont(new Font("Arial", Font.BOLD, (int) nameFontSize));
            g2d.setColor(new Color(0x4d2605));
            g2d.drawString(name, nameX, nameY);

            g2d.setFont(new Font("Arial", Font.BOLD, (int) rewardFontSize));
            g2d.setColor(new Color(0x1b0800));
            g2d.drawString(reward, rewardX, rewardY);
        }
    }

    private void addGradientText(Graphics2D g2d, String text, int x, int y, Font font, Color startColor, Color endColor) {
        // Configura a fonte
        g2d.setFont(font);

        // Calcula o layout do texto
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);
        Rectangle2D bounds = textLayout.getBounds();

        // Cria um BufferedImage para desenhar o texto com gradiente
        BufferedImage textImage = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D textG2D = textImage.createGraphics();
        textG2D.setFont(font);

        // Cria o gradiente
        GradientPaint gradient = new GradientPaint(0, 0, startColor, (float) bounds.getWidth(), (float) bounds.getHeight(), endColor);
        textG2D.setPaint(gradient);

        // Adiciona o texto ao BufferedImage com gradiente
        textG2D.drawString(text, 0, (float) -bounds.getY());
        textG2D.dispose();

        // Desenha o BufferedImage na imagem principal
        g2d.drawImage(textImage, x, y, null);
    }

    private BufferedImage addAgingEffects(BufferedImage image) throws IOException {
        // Cria uma cópia da imagem original
        BufferedImage agedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = agedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);

        // Adiciona uma textura de papel antigo
        BufferedImage paperTexture = ImageIO.read(new ClassPathResource("static/papel_antigo.jpg").getInputStream());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));  // Ajusta a opacidade da textura
        g2d.drawImage(paperTexture, 0, 0, image.getWidth(), image.getHeight(), null);

        // Adiciona um filtro sépia para dar um tom envelhecido
        float[] sepia = {
                0.272f, 0.534f, 0.131f, 0f,
                0.349f, 0.686f, 0.168f, 0f,
                0.393f, 0.769f, 0.189f, 0f,
                0f, 0f, 0f, 1f
        };
        RescaleOp sepiaFilter = new RescaleOp(sepia, new float[4], null);
        g2d.drawImage(agedImage, sepiaFilter, 0, 0);

        // Adiciona bordas desgastadas
        // Aqui você pode adicionar código para desenhar bordas desgastadas, se desejado
        // Exemplo: criar uma máscara de desgaste e aplicá-la com `g2d.drawImage` e `AlphaComposite`

        // Finaliza a edição da imagem
        g2d.dispose();

        return agedImage;
    }

    private void validations(String name) {

        if (name.length() > 10) {
            throw new BadRequestException("Field name is maximum 10 characters", 400);
        }

    }

}
