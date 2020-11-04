package extrator_caracteristicas;

import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AprendizadoMultilayerPerceptron {

	public static double[] multilayerPerceptron(double[]caracteristicas, int trainingTime, double learningRate) {
		double[] retorno = {0,0};
		try {
		
			//*******carregar arquivo de características
			DataSource ds = new DataSource("caracteristicas_audio.arff");
			Instances instancias = ds.getDataSet();
			instancias.setClassIndex(instancias.numAttributes()-1);
			
			MultilayerPerceptron arvore = new MultilayerPerceptron();
			
			arvore.setTrainingTime(trainingTime);
			arvore.setLearningRate(learningRate);
			arvore.setMomentum(0.3);
			arvore.setHiddenLayers("10");
			
			Evaluation eval = new Evaluation(instancias);
			eval.crossValidateModel(arvore, instancias, 30, new Random(1));
			
			arvore.buildClassifier(instancias);
			
			Instance novo = new DenseInstance(instancias.numAttributes());
			novo.setDataset(instancias);
			novo.setValue(0, caracteristicas[0]);
			novo.setValue(1, caracteristicas[1]);
			novo.setValue(2, caracteristicas[2]);
			novo.setValue(3, caracteristicas[3]);
			novo.setValue(4, caracteristicas[4]);
			
			retorno = arvore.distributionForInstance(novo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}
	
}
