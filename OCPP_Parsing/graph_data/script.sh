#!/bin/sh
gnuplot << EOF
set terminal postscript eps color enhanced
set datafile missing
set output "$1.eps"
set xlabel "$2"
set ylabel "$3" 
set title "$4"
plot "$5" using 1:2 $6 title "$7"
EOF



#'unique', 'frequency', 'cumulative', 
#'kdensity', 'acsplines', 'csplines', 'bezier' or 'sbezier'


