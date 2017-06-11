#!/bin/sh
gnuplot << EOF
set terminal svg 
set key outside
set style histogram clustered
set xtics rotate by -30 0 
set title "$5"
set xlabel "$3"
set ylabel "$4"
set boxwidth 1
set style fill solid
set output "$6.svg"
plot  "$1" using 2:xtic(1) title "RANDOM" $2, \
     "$1" using 3 title "NORMAL" $2, \
     "$1" using 4 title "FAULTED" $2
EOF
