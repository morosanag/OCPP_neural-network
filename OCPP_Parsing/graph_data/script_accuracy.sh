#!/bin/sh
gnuplot << EOF
set terminal svg 
set key outside bot center horiz
set output "$1.svg"
set xlabel "$2"
set ylabel "$3" 
set title "$4"
set style line 1 lt 2 lc rgb "red" lw 1
set style line 2 lt 2 lc rgb "blue" lw 1
set style line 3 lt 2 lc rgb "green" lw 1
plot "$5" using 1:2 $6 title "$7" ls 1, \
 	 "$8" using 1:2 $6 title "$9" ls 2, \
     "$10" using 1:2 $6 title "$11" ls 3
EOF