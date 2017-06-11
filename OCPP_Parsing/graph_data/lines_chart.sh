#!/bin/sh
gnuplot << EOF
set terminal svg 
set style histogram clustered
set xtics rotate by -25 0 
set yrange[90:100]
set title "$5"
set xlabel "$3"
set ylabel "$4"
set boxwidth 1
set offset 1, 1
set ytics (90, 91, 92, 92.9, 94, 95, 96, 97, 97.8, 99.2, 1) 
set boxwidth 0.9 absolute
set style fill solid
set output "$6.svg"
plot "$1" using 2:xtic(1) title "$7" $2 
EOF