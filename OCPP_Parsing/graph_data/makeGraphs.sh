#!/bin/sh
./script3.sh FAULTED_TRAFFIC "Ox _ dsf" "Oy dfsd" "FAULTED FAULTED_TRAFFIC" RENOVA_0013_trffic "smooth frequency" "FAULTED" RENOVA_0007_traffic "NORMAL" BMX_traffic "RANDOM"

./script_accuracy.sh RANDOM_ACCURACY "Ox _ dsf" "Oy dfsd" "RANDOM TRAFFIC ACCURACY" BMX_accuracy_both "smooth frequency" "1 + 2" BMX_accuracy_only_first "1" BMX_accuracy_only_second "2" 

./script_accuracy.sh FAULTED_ACCURACY "Ox _ dsf" "Oy dfsd" "FAULTED TRAFFIC ACCURACY" RENOVA_0013_accuracy_both "smooth frequency" "1 + 2" RENOVA_0013_only_first "1" RENOVA_0013_only_second "2" 

./script_accuracy.sh NORMAL_ACCURACY "Ox _ dsf" "Oy dfsd" "NORMAL TRAFFIC ACCURACY" RENOVA_0007_accuracy_both "smooth frequency" "1 + 2" RENOVA_0007_only_first "1" RENOVA_0007_only_second "2" 




#./script3.sh FACULTED_ACCURACY "Ox _ dsf" "Oy dfsd" "FAULTED TRAFFIC ACCURACY" RENOVA_0013_accuracy_both "smooth frequency" "1 + 2" RENOVA_0013_only_first "1" RENOVA_0013_only_second "2" 0.1 1.0





#'unique', 'frequency', 'cumulative', 
#'kdensity', 'acsplines', 'csplines', 'bezier' or 'sbezier'