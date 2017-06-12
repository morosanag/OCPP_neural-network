package com.offnet.ocpp.network;

import com.offnet.ocpp.general.Constants;
import com.offnet.ocpp.general.Utils;
import java.util.ArrayList;


public class NeuralNetwork {	
	
	double hiddens[];
	double hiddens_errors[];
	double outputs[];
	double output_errors[];
	double level1[][];
	double level2[][];
	// 0.6
	
	
        public NeuralNetwork() {
            
        }
        
        void initialize(NeuralNetInput neuralNetInput) {
            initialize(NeuralNetInput.INPUT_SIZE, NeuralNetInput.HIDDEN_SIZE, NeuralNetInput.OUTPUT_SIZE);
        }
	
	void initialize(int nr_inputs, int nr_hidden, int nr_outputs) {
	
            hiddens = new double[nr_hidden];
		hiddens_errors = new double[nr_hidden];
		outputs = new double[nr_outputs];
		output_errors = new double[nr_outputs];
		level1 = new double[nr_inputs][];
		
		
		for(int i = 0; i < nr_inputs; i++) {
			level1[i] = new double[nr_hidden];
		}
		
		level2 = new double[nr_hidden][];
		for(int i = 0; i < nr_hidden; i++) {
			level2[i] = new double[nr_outputs];
		}
		
		// initializeaza ponderile
		for(int i = 0; i < nr_inputs; i++) {
			for(int j = 0; j < nr_hidden; j++) {
				level1[i][j] = 0.05;
			}
		}
		
		for(int i = 0; i < nr_hidden; i++) {
			for(int j = 0; j < nr_outputs; j++) {
				level2[i][j] = 0.05;
			}
		}
                
                for(int i = 0; i < hiddens.length; i++) {
			hiddens[i] = 0.05;
		}
		
		for(int i = 0; i < outputs.length; i++) {
			outputs[i] = 0.05;
		}
                
	}
        
        public void printCurrentState() {
            System.out.println("level1: ");
            for(int i = 0; i < level1.length; i++) {
                for(int j = 0; j < level1[0].length; j++) {
                    System.out.print(level1[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
            
            System.out.println("hiddens");
            for(int i = 0; i < hiddens.length; i++) {
                System.out.print(hiddens[i] + " ");
            }
            System.out.println();
            
            System.out.println("level2: ");
            for(int i = 0; i < level2.length; i++) {
                for(int j = 0; j < level2[0].length; j++) {
                    System.out.print(level2[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
	
	// functia de activare - tangent hiperbolic
	double activate(double value) {
            
//            return value;
return (1/( 1 + Math.pow(Math.E,(-1*value))));
            
            /*
		return Math.tanh(value);*/
	}
        
        double[] activateArray(double[] array) {
            double[] resultArray = new double[array.length];
            for(int i = 0; i < array.length; i++) {
                resultArray[i] = activate(array[i]);
            }
            return resultArray;
        }
        
	
        public void runLearningSet(double inputs[], double targets[]) {
            double temp = 0;
            double error = 0;
        
            for(int j = 0; j < hiddens.length; j++) {
                hiddens[j] = 0;
                for(int i = 0; i < inputs.length; i++) {
                    hiddens[j] += level1[i][j] * inputs[i];
                }
                hiddens = activateArray(hiddens);
            }
            
            /*System.out.println("HIDDENS:");
            for(int j = 0; j < hiddens.length; j++) {
                System.out.print(hiddens[j] + " ");
            }
            System.out.println();
            */
            for(int j = 0; j < outputs.length; j++) {
                outputs[j] = 0;
                for(int i = 0; i < hiddens.length; i++) {
                    outputs[j] += level2[i][j] * hiddens[i];
                }
                outputs = activateArray(outputs);
            }
            /*
            System.out.println("OUTPUTS:");
            for(int j = 0; j < outputs.length; j++) {
                System.out.print(outputs[j] + " ");
            }
            System.out.println();
            */
            for(int j = 0; j < outputs.length; j++) {
                output_errors[j] = outputs[j] * (1 - outputs[j]) * (targets[j] - outputs[j]);
            }
            
            /*System.out.println("OUTPUTS_ERRORS:");
            for(int j = 0; j < output_errors.length; j++) {
                System.out.print(output_errors[j] + " ");
            }
            System.out.println();
            */
            for(int j = 0; j < hiddens_errors.length; j++) {
                hiddens_errors[j] = 0;
                for(int i = 0; i < output_errors.length; i++) {
                    hiddens_errors[j] += output_errors[i] * level2[j][i];
                }
                hiddens_errors[j] *= hiddens[j] * (1 - hiddens[j]);
            }
            
            /*System.out.println("HIDDENS_ERRORS:");
            for(int j = 0; j < hiddens_errors.length; j++) {
                System.out.print(hiddens_errors[j] + " ");
            }
            System.out.println();
            */
            double[][] level1_temp = new double[level1.length][level1[0].length];
            double[][] level2_temp = new double[level2.length][level2[0].length];
            
            
            for(int i = 0; i < level2_temp.length; i++) {
                for(int j = 0; j < level2_temp[0].length; j++) {
                    level2_temp[i][j] = Constants.LEARN_RATE * output_errors[j] * hiddens[i] + level2[i][j];
                }
            }
            
            for(int i = 0; i < level1_temp.length; i++) {
                for(int j = 0; j < level1_temp[0].length; j++) {
                   // System.out.println("a1: " + level1[i][j] + " + " + (learn_rate * hiddens_errors[j] * inputs[i]));
                   // System.out.println("a2: " + (level1[i][j] + learn_rate * hiddens_errors[j] * inputs[i]));
                    
                    level1_temp[i][j] = level1[i][j] + Constants.LEARN_RATE * hiddens_errors[j] * inputs[i];
                   // System.out.println("level1_temp[" + i + "][" + j + "]= " + level1_temp[i][j]);
                   // System.out.println("as: " + level1_temp[i][j] + " = " + (learn_rate * hiddens_errors[j] * inputs[i]) + " + " + level1[i][j]);
                }
            }
            
            for(int i = 0; i < level2_temp.length; i++) {
                for(int j = 0; j < level2_temp[0].length; j++) {
                    level2[i][j] = level2_temp[i][j];
                }
            }
            
            for(int i = 0; i < level1_temp.length; i++) {
                for(int j = 0; j < level1_temp[0].length; j++) {
                    level1[i][j] = level1_temp[i][j];
                }
            }
            
            /*System.out.println("LEVEL2:");
            for(int i = 0; i < level2_temp.length; i++) {
                for(int j = 0; j < level2_temp[0].length; j++) {
                    System.out.println(level2[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
            */
           // level1 = level1_temp;
            
            /*System.out.println("LEVEL1:");
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    level1[i][j] = level1_temp[i][j];
                    System.out.println("level1[" + i + "][" + j + "]= " + level1[i][j]);
                }
            }
            
            System.out.println("LEVEL1:");
            for(int i = 0; i < level1.length; i++) {
                for(int j = 0; j < level1[0].length; j++) {
                    System.out.println(level1_temp[i][j]);
                }
                System.out.println();
            }
            System.out.println();
            
            */
            
            //printCurrentState();
            
        }
        
        /*
	public void runLearningSet(double inputs[], double targets[]) {
	
		double temp = 0;
		double error = 0;

		// calculeaza valorile neuronilor de pe stratul ascuns
		for(int i = 0; i < inputs.length; i++) {
			for(int j = 0; j < hiddens.length; j++) {
				hiddens[j] += inputs[i] * level1[i][j];
			}
		}
		
		// aplica functia de activare pe stratul ascuns
		for(int i = 0; i < hiddens.length; i++) {
			hiddens[i] = activate(hiddens[i]);
		}
		
		// calculeaza outputurile
		for(int i = 0; i < hiddens.length; i++) {
			for(int j = 0; j < outputs.length; j++) {
				outputs[j] += hiddens[i] * level2[i][j];
			}
		}

		// aplica functia de activare pe output-uri
		for(int i = 0; i < outputs.length; i++) {
			outputs[i] = activate(outputs[i]);
		}
		
		// erorile de la iesire
		for(int i = 0; i < outputs.length; i++) {
			output_errors[i] = outputs[i] * (1 - outputs[i]) * (targets[i] - outputs[i]); 
                                //(targets[i] - outputs[i]);// *	(1 - outputs[i]) * outputs[i];
		}
		
		// calculeaza erorile de ps stratul ascuns
		for(int i = 0; i < hiddens_errors.length; i++) {
			temp = 0;
			for(int j = 0; j < outputs.length; j++) {
				temp += level2[i][j] * output_errors[j];
			}
			hiddens_errors[i] = hiddens[i] * (1 - hiddens[i]) * temp;
		}
                
		
                for(int i = 0; i < inputs.length; i++) {
			for(int j = 0; j < hiddens.length; j++) {
                            level2[i][j] = learn_rate * output_errors[j] * hiddens[i] + level2[i][j];
                            level1[i][j] = learn_rate * hiddens_errors[j] * inputs[i] + level1[i][j];
                        }
                }
        }*/
        
        
        public double[] runSet(double inputs[]) {
            double temp = 0;
		double error = 0;
		
                double[] hiddens_temp = new double[hiddens.length];
                double[] outputs_temp = new double[outputs.length];
                
		//initialize();

		// calculeaza valorile neuronilor de pe stratul ascuns
		for(int i = 0; i < inputs.length; i++) {
			for(int j = 0; j < hiddens_temp.length; j++) {
				hiddens_temp[j] += inputs[i] * level1[i][j];
			}
		}
		
		// aplica functia de activare pe stratul ascuns
		for(int i = 0; i < hiddens_temp.length; i++) {
			hiddens_temp[i] = activate(hiddens_temp[i]);
		}
		
		// calculeaza outputurile
		for(int i = 0; i < hiddens_temp.length; i++) {
			for(int j = 0; j < outputs_temp.length; j++) {
				outputs_temp[j] += hiddens_temp[i] * level2[i][j];
			}
		}

		// aplica functia de activare pe output-uri
		for(int i = 0; i < outputs_temp.length; i++) {
			outputs_temp[i] = activate(outputs_temp[i]);
		}
                
                return outputs_temp;
        }
        
        public void processNode(NeuralNetInput neuralNetInput) {
            if(level1 == null) {
                initialize(neuralNetInput);
            }
            
            double[] inputs = new double[NeuralNetInput.INPUT_SIZE];
            double[] targets = new double[NeuralNetInput.OUTPUT_SIZE];
            
            inputs[0] = neuralNetInput.getLastTimeRequestPerc();
            inputs[1] = neuralNetInput.getLastTimeStationRequestPerc();
           // inputs[2] = neuralNetInput.getSequenceIndex(neuralNetInput.getRequestSequence());
            
            for(int i = 0; i < neuralNetInput.getRequestSequence().size(); i++) {
                try {
                    inputs[2 + i] = neuralNetInput.getRequestSequence().get(i).getPriority();
                } catch (NullPointerException ex) {
                    return;
                }
            }
            
            for(int i = 0; i < inputs.length; i++) {
            //	System.out.print(inputs[i] + " ");
            }
            //System.out.println();
            
                targets[0] = neuralNetInput.getTarget();
            
          //  System.out.println(!Utils.checkRequestSequence(neuralNetInput.getRequestSequence()) + ": " + neuralNetInput.getRequestSequence());
                
            if(!Utils.checkRequestSequence(neuralNetInput.getRequestSequence())) {
                targets[0] = 0;
            }
            
           // System.out.println("inputs:");
         /*   for(int i = 0; i < inputs.length; i++) {
                System.out.print(inputs[i] + " ");
            }
            System.out.println();
           */ 
            /*System.out.println("targets:");
            for(int i = 0; i < targets.length; i++) {
                System.out.print(targets[i] + " ");
            }
            System.out.println();
            */
            runLearningSet(inputs, targets);
            
        }
		
        public void computeNode(NeuralNetInput neuralNetInput) {
            if(level1 == null) {
                initialize(neuralNetInput);
            }
            
            double[] inputs = new double[NeuralNetInput.INPUT_SIZE];
            double[] targets = new double[NeuralNetInput.OUTPUT_SIZE];
            
            inputs[0] = neuralNetInput.getLastTimeRequestPerc();
            inputs[1] = neuralNetInput.getLastTimeStationRequestPerc();
            
             for(int i = 0; i < neuralNetInput.getRequestSequence().size(); i++) {
                 if(neuralNetInput.getRequestSequence().get(i) != null) {
            	inputs[1 + i] = neuralNetInput.getRequestSequence().get(i).getPriority();
                 }
            }
                    
                 targets[0] = neuralNetInput.getTarget();
            
            if(!Utils.checkRequestSequence(neuralNetInput.getRequestSequence())) {
                targets[0] /= 2;
            }
             
            targets = runSet(inputs);
            neuralNetInput.setTarget(targets[0]);
            
        }
}
