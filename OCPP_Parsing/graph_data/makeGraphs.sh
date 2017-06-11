#!/bin/sh
# DONE
./script3.sh ALL_STATIONS_TRAFFIC "" "No of requests" "" RENOVA_0013_trffic "smooth frequency" "FAULTED" RENOVA_0007_traffic "NORMAL" BMX_traffic "RANDOM"

./script_accuracy.sh RANDOM_ACCURACY "Threshold" "Accuracy" "" BMX_accuracy_both "smooth frequency" "FAULT + RAND" BMX_accuracy_only_first "FAULT" BMX_accuracy_only_second "RAND"

./script_accuracy.sh FAULTED_ACCURACY "Threshold" "Accuracy" "" RENOVA_0013_accuracy_both "smooth frequency" "FAULT + RAND" RENOVA_0013_only_first "FAULT" RENOVA_0013_only_second "RAND" 

./script_accuracy.sh NORMAL_ACCURACY "Threshold" "Accuracy" "" RENOVA_0007_accuracy_both "smooth frequency" "FAULT + RAND" RENOVA_0007_only_first "FAULT" RENOVA_0007_only_second "RAND" 

./bar_chart.sh no_requests_per_type_per_station.txt "with histograms" "" "No of requests" "No of request per type" NO_REQUEST_PER_TYPE

./lines_chart.sh detection_rate_3_cases.txt "with linespoints" "" "Accuracy (%)" "" BEST_ACCURACY ""






#'unique', 'frequency', 'cumulative', 
#'kdensity', 'acsplines', 'csplines', 'bezier' or 'sbezier'

#./script3.sh bmx_hidden "Ox _ dsf" "Oy dfsd" "FAULTED FAULTED_TRAFFIC" bmx_hidden_input.txt  "smooth bezier" "FAULTED" bmx_hidden_first.txt  "NORMAL" bmx_hidden_second.txt "RANDOM"

#./script3.sh renova_0013_hidden "Ox _ dsf" "Oy dfsd" "FAULTED FAULTED_TRAFFIC" renova_0013_hidden_both.txt  "smooth bezier" "FAULTED" renova_0013_hidden_first.txt  "NORMAL" renova_0013_hidden_second.txt "RANDOM"

#./script3.sh renova_0007_hidden "Ox _ dsf" "Oy dfsd" "FAULTED FAULTED_TRAFFIC" renova_0007_hidden_both.txt  "smooth bezier" "FAULTED" renova_0007_hidden_first.txt  "NORMAL" renova_0007_hidden_second.txt "RANDOM"

#./script3.sh hidden_layer_size "Ox _ dsf" "Oy dfsd" "FAULTED FAULTED_TRAFFIC" renova_0013_hidden_both.txt  "with linespoints" "FAULTED" bmx_hidden_input.txt  "RANDOM" renova_0007_hidden_both.txt "NORMAL"

#./script3.sh FACULTED_ACCURACY "Ox _ dsf" "Oy dfsd" "FAULTED TRAFFIC ACCURACY" RENOVA_0013_accuracy_both "smooth frequency" "1 + 2" RENOVA_0013_only_first "1" RENOVA_0013_only_second "2" 0.1 1.0