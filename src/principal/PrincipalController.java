package principal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.sound.sampled.UnsupportedAudioFileException;

import audio.exception.FileFormatNotSupportedException;
import audio.wavfile.WavFileException;
import extrator_caracteristicas.AprendizadoJ48;
import extrator_caracteristicas.AprendizadoMultilayerPerceptron;
import extrator_caracteristicas.AprendizadoNaiveBaye;
import extrator_caracteristicas.ExtractCaracteristicaAudio;
import extrator_caracteristicas.ExtractCaracteristicasSimpsons;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class PrincipalController implements Initializable {

	// SIMPSONS
	@FXML
	private ImageView imageView;

	@FXML
	private Label milhouseCabeloAzul = new Label("");
	@FXML
	private Label milhouseBermudaSapatoVermelho = new Label("");
	@FXML
	private Label nedCabeloBigodeMarrom = new Label("");
	@FXML
	private Label nedSweaterVerde = new Label("");

	@FXML
	private Label percentNed = new Label("");
	@FXML
	private Label percentMilhouse = new Label("");

	@FXML
	private Label percentNedJ48 = new Label("");
	@FXML
	private Label percentMilhouseJ48 = new Label("");

	// AUDIO
	@FXML
	private TextField taxaAprendizagem = new TextField("");
	@FXML
	private TextField cicloTreinamento = new TextField("");
	@FXML
	private TextArea consoleAudio = new TextArea("");
	@FXML
	private Label arquivoSelecionado = new Label("");
	@FXML
	private Label animalIdentificado = new Label("");
	@FXML
	private Label magnitude = new Label("");
	@FXML
	private Label espectograma = new Label("");
	@FXML
	private Label stft = new Label("");
	@FXML
	private Label mccf = new Label("");
	
	private File audioSelecionado;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		this.cicloTreinamento.setText("5000");
		this.taxaAprendizagem.setText("0.2");
	}

	@FXML
	public void extrairCaracteristicas() {
		ExtractCaracteristicasSimpsons.extrair();
	}

	@FXML
	public void extrairCaracteristicasAudio() {
		try {
			ExtractCaracteristicaAudio.extrair();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WavFileException e) {
			e.printStackTrace();
		} catch (FileFormatNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void selecionaImagem() {
		File f = buscaImg();
		if (f != null) {
			Image img = new Image(f.toURI().toString());
			imageView.setImage(img);
			imageView.setFitWidth(img.getWidth());
			imageView.setFitHeight(img.getHeight());
			double[] caracteristicas = ExtractCaracteristicasSimpsons.extraiCaracteristicas(f);

			this.nedCabeloBigodeMarrom.setText(caracteristicas[0] + "%");
			this.nedSweaterVerde.setText(caracteristicas[1] + "%");
			this.milhouseCabeloAzul.setText(caracteristicas[2] + "%");
			this.milhouseBermudaSapatoVermelho.setText(caracteristicas[3] + "%");

			double[] percentsNaiveBayes = AprendizadoNaiveBaye.naiveBayes(caracteristicas);

			DecimalFormat df = new DecimalFormat("0.00000");

			this.percentNed.setText(df.format(percentsNaiveBayes[0] * 100) + "%");
			this.percentMilhouse.setText(df.format(percentsNaiveBayes[1] * 100) + "%");

			double[] percentsJ48 = AprendizadoJ48.j48(caracteristicas);

			this.percentNedJ48.setText(df.format(percentsJ48[0] * 100) + "%");
			this.percentMilhouseJ48.setText(df.format(percentsJ48[1] * 100) + "%");

		}
	}

	@FXML
	public void iniciarClassificacao() {

		if (this.audioSelecionado != null) {
			double[] caracteristicas;
			try {
				
				this.consoleAudio.setText("-------------- INICIADO A CLASSIFICAÇÃO, AGUARDE --------------");
				
				caracteristicas = ExtractCaracteristicaAudio.extraiCaracteristicas(this.audioSelecionado);
				
				this.consoleAudio.setText("-------------- CARACTERISTICAS --------------");
				
				for (double d : caracteristicas) {
					this.consoleAudio.setText(this.consoleAudio.getText() + "\n\n " + d);
				}
				
				this.magnitude.setText(caracteristicas[0] + "");
				this.espectograma.setText(caracteristicas[1] + "");
				this.stft.setText(caracteristicas[2] + "");
				this.mccf.setText(caracteristicas[3] + "");
				
				double[] percentsNaiveBayes = AprendizadoNaiveBaye.naiveBayes(caracteristicas);
				
				DecimalFormat df = new DecimalFormat("0.00000");
				
				this.consoleAudio.setText(
						this.consoleAudio.getText() + "\n\n -------------- PORCENTAGEM NAIVE BAYES AUDIO --------------");
				
				this.consoleAudio.setText(
						this.consoleAudio.getText() + "\n\n Gato: " + df.format(percentsNaiveBayes[0] * 100) + "%");
				this.consoleAudio.setText(
						this.consoleAudio.getText() + "\n\n Cachorro: " + df.format(percentsNaiveBayes[1] * 100) + "%");
				
				double[] percentsJ48 = AprendizadoJ48.j48(caracteristicas);
				
				this.consoleAudio
				.setText(this.consoleAudio.getText() + "\n\n -------------- PORCENTAGEM J48 AUDIO --------------");
				
				this.consoleAudio
				.setText(this.consoleAudio.getText() + "\n\n Gato: " + df.format(percentsJ48[0] * 100) + "%");
				this.consoleAudio
				.setText(this.consoleAudio.getText() + "\n\n Cachorro: " + df.format(percentsJ48[1] * 100) + "%");
				
				double[] percentsPerceptron = AprendizadoMultilayerPerceptron.multilayerPerceptron(caracteristicas,
						Integer.parseInt(cicloTreinamento.getText()), Double.parseDouble(taxaAprendizagem.getText()));
				
				this.consoleAudio.setText(this.consoleAudio.getText()
						+ "\n\n -------------- PORCENTAGEM MULTILAYER PERCEPTRON --------------");
				
				double porcentagemGato = percentsPerceptron[0];
				double porcentagemCachorro = percentsPerceptron[1];
				
				this.consoleAudio.setText(
						this.consoleAudio.getText() + "\n\n Gato: " + df.format(percentsPerceptron[0] * 100) + "%");
				this.consoleAudio.setText(
						this.consoleAudio.getText() + "\n\n Cachorro: " + df.format(percentsPerceptron[1] * 100) + "%");
				
				if (porcentagemGato > porcentagemCachorro)
					this.animalIdentificado.setText("Gato");
				else
					this.animalIdentificado.setText("Cachorro");
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				this.consoleAudio.setText("-------------- ERRO AO CLASSIFICAR --------------");
			} catch (IOException e) {
				e.printStackTrace();
				this.consoleAudio.setText("-------------- ERRO AO CLASSIFICAR --------------");
			} catch (WavFileException e) {
				e.printStackTrace();
				this.consoleAudio.setText("-------------- ERRO AO CLASSIFICAR --------------");
			} catch (FileFormatNotSupportedException e) {
				e.printStackTrace();
				this.consoleAudio.setText("-------------- ERRO AO CLASSIFICAR --------------");
			}
		} else {
			this.consoleAudio.setText("-------------- POR FAVOR, SELECIONE UM AUDIO --------------");
		}
	}

	@FXML
	public void selecionaAudio() {
		this.audioSelecionado = buscaAudio();
		if (this.audioSelecionado != null) {
			this.arquivoSelecionado.setText(this.audioSelecionado.getName());
		}
	}

	private File buscaImg() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.JPG", "*.png",
				"*.PNG", "*.gif", "*.GIF", "*.bmp", "*.BMP"));
		fileChooser.setInitialDirectory(new File("src/imagens"));
		File imgSelec = fileChooser.showOpenDialog(null);
		try {
			if (imgSelec != null) {
				return imgSelec;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private File buscaAudio() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Áudios", "*.wav"));
		fileChooser.setInitialDirectory(new File("src/audios"));
		File audioSelec = fileChooser.showOpenDialog(null);
		try {
			if (audioSelec != null) {
				return audioSelec;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
