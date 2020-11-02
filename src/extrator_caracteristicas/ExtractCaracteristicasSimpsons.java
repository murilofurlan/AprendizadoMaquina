package extrator_caracteristicas;

import java.io.File;
import java.io.FileOutputStream;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import enumered.PersonagemSimpsomEnum;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ExtractCaracteristicasSimpsons {

	public static double[] extraiCaracteristicas(File f) {

		double[] caracteristicas = new double[5];

		double marromCabeloBigodeNed = 0;
		double verdeSweaterNed = 0;

		double azulCabeloMilhouse = 0;
		double vermelhoBermudaSapatoMilhouse = 0;

		Image img = new Image(f.toURI().toString());
		PixelReader pr = img.getPixelReader();

		Mat imagemOriginal = Imgcodecs.imread(f.getPath());
		Mat imagemProcessada = imagemOriginal.clone();

		int w = (int) img.getWidth();
		int h = (int) img.getHeight();

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {

				Color cor = pr.getColor(j, i);

				double r = cor.getRed() * 255;
				double g = cor.getGreen() * 255;
				double b = cor.getBlue() * 255;

				// NED
				if (i < (h / 2) && isCabeloBigodeNed(r, g, b)) {
					marromCabeloBigodeNed++;
					imagemProcessada.put(i, j, new double[] { 0, 255, 255 });
				}

				if (i > (h / 3) && isSweaterNed(r, g, b)) {
					verdeSweaterNed++;
					imagemProcessada.put(i, j, new double[] { 0, 255, 255 });
				}

				// MILHOUSE
				if (i < (h / 2 + h / 3) && isCabeloMilhouse(r, g, b)) {
					azulCabeloMilhouse++;
					imagemProcessada.put(i, j, new double[] { 0, 255, 255 });
				}

				if (i > (h / 2) && isBermudaSapatoMilhouse(r, g, b)) {
					vermelhoBermudaSapatoMilhouse++;
					imagemProcessada.put(i, j, new double[] { 0, 255, 255 });
				}

			}
		}

		marromCabeloBigodeNed = (marromCabeloBigodeNed / (w * h)) * 100;
		verdeSweaterNed = (verdeSweaterNed / (w * h)) * 100;

		azulCabeloMilhouse = (azulCabeloMilhouse / (w * h)) * 100;
		vermelhoBermudaSapatoMilhouse = (vermelhoBermudaSapatoMilhouse / (w * h)) * 100;

		caracteristicas[0] = marromCabeloBigodeNed;
		caracteristicas[1] = verdeSweaterNed;

		caracteristicas[2] = azulCabeloMilhouse;
		caracteristicas[3] = vermelhoBermudaSapatoMilhouse;

		// APRENDIZADO SUPERVISIONADO - JÁ SABE QUAL A CLASSE NAS IMAGENS DE TREINAMENTO

		char initialImage = f.getName().charAt(0);

		if (initialImage == 'n')
			caracteristicas[4] = PersonagemSimpsomEnum.NED.getValue();
		else if (initialImage == 'm')
			caracteristicas[4] = PersonagemSimpsomEnum.MILHOUSE.getValue();

//		HighGui.imshow("Imagem original", imagemOriginal);
//		HighGui.imshow("Imagem processada", imagemProcessada);
//
//		HighGui.waitKey(0);

		return caracteristicas;
	}

	// NED
	public static boolean isCabeloBigodeNed(double r, double g, double b) {
		if (b >= 33 && b <= 55 && g >= 62 && g <= 104 && r >= 88 && r <= 149) {
			return true;
		}
		return false;
	}

	public static boolean isSweaterNed(double r, double g, double b) {
		if (b >= 23 && b <= 44 && g >= 68 && g <= 131 && r >= 40 && r <= 77) {
			return true;
		}
		return false;
	}

	// MILHOUSE
	public static boolean isCabeloMilhouse(double r, double g, double b) {
		if (b >= 159 && b <= 216 && g >= 63 && g <= 117 && r >= 38 && r <= 92) {
			return true;
		}
		return false;
	}

	public static boolean isBermudaSapatoMilhouse(double r, double g, double b) {
		if (b >= 15 && b <= 28 && g >= 34 && g <= 53 && r >= 168 && r <= 234) {
			return true;
		}
		return false;
	}

	public static void extrair() {

		// Cabeçalho do arquivo Weka
		String exportacao = "@relation caracteristicas\n\n";
		exportacao += "@attribute marrom_cabelo_bigode_ned real\n";
		exportacao += "@attribute verde_sweater_ned real\n";
		exportacao += "@attribute azul_cabelo_milhouse real\n";
		exportacao += "@attribute vermelho_bermuda_sapato_milhouse real\n";
		exportacao += "@attribute classe {Ned, Milhouse}\n\n";
		exportacao += "@data\n";

		// Diretório onde estão armazenadas as imagens
		File diretorio = new File("src\\imagens");
		File[] arquivos = diretorio.listFiles();

		// Definição do vetor de características
		double[][] caracteristicas = new double[arquivos.length][5];

		// Percorre todas as imagens do diretório
		int cont = -1;
		for (File imagem : arquivos) {
			cont++;
			caracteristicas[cont] = extraiCaracteristicas(imagem);

			String classe = PersonagemSimpsomEnum.getClassePerValue(caracteristicas[cont][4]);

			System.out.println("Marrom cabelo/bigode Ned: " + caracteristicas[cont][0] + " - Verde sweater Ned: "
					+ caracteristicas[cont][1] + " - Azul cabelo Milhouse: " + caracteristicas[cont][2]
					+ " - Vermelho bermuda/sapato Milhouse: " + caracteristicas[cont][3] + " - Classe: " + classe);

			exportacao += caracteristicas[cont][0] + "," + caracteristicas[cont][1] + "," + caracteristicas[cont][2]
					+ "," + caracteristicas[cont][3] + "," + classe + "\n";
		}

		// Grava o arquivo ARFF no disco
		try {
			File arquivo = new File("caracteristicas.arff");
			FileOutputStream f = new FileOutputStream(arquivo);
			f.write(exportacao.getBytes());
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
