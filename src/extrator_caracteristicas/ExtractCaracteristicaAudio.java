package extrator_caracteristicas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.complex.Complex;

import audio.exception.FileFormatNotSupportedException;
import audio.wavfile.WavFile;
import audio.wavfile.WavFileException;
import enumered.GatoCachorroEnum;

public class ExtractCaracteristicaAudio {

	public static void extrair()
			throws UnsupportedAudioFileException, IOException, WavFileException, FileFormatNotSupportedException {

		// Cabeçalho do arquivo Weka
		String exportacao = "@relation caracteristicas\n\n";
		exportacao += "@attribute magnitude real\n";
		exportacao += "@attribute espectrograma real\n";
		exportacao += "@attribute stft real\n";
		exportacao += "@attribute mccf real\n";
		exportacao += "@attribute classe {Gato, Cachorro}\n\n";
		exportacao += "@data\n";

		// Diretório onde estão armazenadas os áudios
		File diretorio = new File("src\\audios");
		File[] arquivos = diretorio.listFiles();

		// Definição do vetor de características
		double[][] caracteristicas = new double[280][4];

		// Percorre todos os áudios do diretório
		int cont = -1;
		for (File audio : arquivos) {
			cont++;

			caracteristicas[cont] = extraiCaracteristicas(audio);

			String classe = GatoCachorroEnum.getClassePerValue(caracteristicas[cont][4]);

			exportacao += caracteristicas[cont][0] + "," + caracteristicas[cont][1] + "," + caracteristicas[cont][2]
					+ "," + caracteristicas[cont][3] + "," + classe + "\n";
		}

		// Grava o arquivo ARFF no disco
		try {
			File arquivo = new File("caracteristicas_audio.arff");
			FileOutputStream f = new FileOutputStream(arquivo);
			f.write(exportacao.getBytes());
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static double[] extraiCaracteristicas(File f)
			throws UnsupportedAudioFileException, IOException, WavFileException, FileFormatNotSupportedException {

		double[] caracteristicas = new double[5];

		double caracteristica1 = 0; // Magnitude
		double caracteristica2 = 0; // Espectograma
		double caracteristica3 = 0; // STFT
		double caracteristica4 = 0; // MCCF

		// Extração de Caracteristicas

		float[][] magnitude = new float[0][0];
		magnitude = extraiMagnitude(f);

		caracteristica1 = magnitude[0][0];

		float[] MagnitudeFeature = new float[0];
		MagnitudeFeature = extraiMagnitudeFeature(f, magnitude);

		JLibrosa jLibrosa2 = new JLibrosa();
		WavFile wavFile = WavFile.openWavFile(f);
		;
		int mSampleRate = (int) wavFile.getSampleRate();
		wavFile.close();
		float[][] melSpectrogramGerado = jLibrosa2.generateMelSpectroGram(MagnitudeFeature, mSampleRate, 2048, 128,
				256);

		caracteristica2 = melSpectrogramGerado[0][0];

		JLibrosa jLibrosa3 = new JLibrosa();
		WavFile wavFile3 = WavFile.openWavFile(f);
		;
		int mSampleRate3 = (int) wavFile3.getSampleRate();
		wavFile3.close();
		Complex[][] stftComplexValues = jLibrosa3.generateSTFTFeatures(MagnitudeFeature, mSampleRate3, 40);

		caracteristica3 = stftComplexValues[0].length;

		JLibrosa jLibrosa4 = new JLibrosa();
		WavFile wavFile4 = WavFile.openWavFile(f);
		;
		int mSampleRate4 = (int) wavFile4.getSampleRate();
		wavFile4.close();
		float[][] mfccValues = jLibrosa4.generateMFCCFeatures(MagnitudeFeature, mSampleRate4, 40);

		caracteristica4 = mfccValues[0].length;

		// Fim da Extração

		caracteristicas[0] = caracteristica1;
		caracteristicas[1] = caracteristica2;
		caracteristicas[2] = caracteristica3;
		caracteristicas[3] = caracteristica4;

		// APRENDIZADO SUPERVISIONADO - JÁ SABE QUAL A CLASSE NOS AUDIOS DE TREINAMENTO
		caracteristicas[4] = f.getName().charAt(0) == 'c' ? 0 : 1;

		return caracteristicas;
	}

	public static float[][] extraiMagnitude(File f) throws IOException, WavFileException {

		// Caracteristica 1

		int BUFFER_SIZE = 4096;
		int taxaAmostragem = -1;

		WavFile wavFile = null;
		wavFile = WavFile.openWavFile(f);
		int numeroFrames = (int) (wavFile.getNumFrames());
		int mSampleRate = (int) wavFile.getSampleRate();
		int mChannels = wavFile.getNumChannels();

		taxaAmostragem = mSampleRate;
		if (taxaAmostragem != -1) {
			mSampleRate = taxaAmostragem;
		}

		float[][] buffer = new float[mChannels][numeroFrames];
		int frameOffset = 0;
		int loopCounter = ((numeroFrames * mChannels) / BUFFER_SIZE) + 1;
		for (int i = 0; i < loopCounter; i++) {
			frameOffset = wavFile.readFrames(buffer, numeroFrames, frameOffset);
		}

		if (wavFile != null) {
			wavFile.close();
		}

		return buffer;
	}

	public static float[] extraiMagnitudeFeature(File f, float[][] magnitude)
			throws IOException, WavFileException, FileFormatNotSupportedException {

		WavFile wavFile = WavFile.openWavFile(f);

		int numeroFrames = (int) (wavFile.getNumFrames());
		int numeroCanais = wavFile.getNumChannels();
		wavFile.close();

		DecimalFormat df = new DecimalFormat("#,#####");
		df.setRoundingMode(RoundingMode.CEILING);

		float[] espectograma = new float[numeroFrames];

		for (int q = 0; q < numeroFrames; q++) {
			double valorFrame = 0;
			for (int p = 0; p < numeroCanais; p++) {
				valorFrame = valorFrame + magnitude[p][q];
			}
			espectograma[q] = Float.parseFloat(df.format(valorFrame / numeroCanais));
		}

		return espectograma;
	}

}
