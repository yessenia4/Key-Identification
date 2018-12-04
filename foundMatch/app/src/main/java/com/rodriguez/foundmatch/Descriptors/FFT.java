package com.rodriguez.foundmatch.Descriptors;

//Code was obtained and based from Dr.Quwieder providing in Spring 2018 for Image Proecessing class
//and https://www.youtube.com/watch?v=htCj9exbGo0

public class FFT {
    private static final Complex ZERO = new Complex(0, 0);

    //F.k = (N/2-1)Σ(m=0) (x.2m)(e^(-j2pikm/(N/2))) + (e^(-jpik/(N/2)))(N/2-1)Σ(m=0) (x.2m+1)(e^(-j2pikm/(N/2)))
    public static Complex[] FFT(Complex[] x){
        int N = x.length;

        //base case
        if(N == 1)
            return new Complex[] {x[0]};

        //N has to be a power of 2
        if(N % 2 != 0)
            throw new IllegalArgumentException("N is not a power of 2");

        //Get N/2
        int M = N/2;

        //Get Even and Odd terms
        Complex[] Xeven = new Complex[M];
        Complex[] Xodd = new Complex[M];

        for (int k=0; k<M; k++){
            Xeven[k] = x[2*k];
            Xodd[k] = x[2*k+1];
        }

        Complex[] Feven = FFT(Xeven);
        Complex[] Fodd = FFT(Xodd);

        //Combine Values
        Complex[] combF = new Complex[N];
        for(int k=0;k<M;k++){
            double kth = -2*Math.PI*k/N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            Complex exponential = wk.times(Fodd[k]);


            combF[k] = Feven[k].plus(exponential);
            combF[k+N/2] = Feven[k].minus(exponential);
        }

        return combF;
    }

    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = FFT(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    /*
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }

    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex[] a = new Complex[2*x.length];
        for (int i = 0; i < x.length; i++)
            a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++)
            a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0; i < y.length; i++)
            b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++)
            b[i] = ZERO;

        return cconvolve(a, b);
    }

    // display an array of Complex numbers to standard output
    private static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }*/
}
