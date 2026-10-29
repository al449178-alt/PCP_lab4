import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class EjemploNumeroPI {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    long                        t1, t2;
    double                      tSec, tPar;
    Acumula                     a;
    MiHebraMultAcumulaciones    vt[];

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( numRectangulos <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 ) y ( numRectangulos > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Inicio del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo secuencial (s.):        " + tSec );

    //
    // Calculo del numero PI de forma paralela:
    // Multiples acumulaciones por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra." );
    t1 = System.nanoTime();

    // (A) ...
    a = new Acumula();
    vt = new MiHebraMultAcumulaciones[ numHebras ];

    for( int i = 0; i < numHebras; i++ ) {
      vt[ i ] = new MiHebraMultAcumulaciones(
              i, numHebras, numRectangulos, a );
      vt[ i ].start();
    }
    try {
      for( int i = 0; i < numHebras; i++ ) {
        vt[ i ].join();
      }
    } catch( InterruptedException ex ) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + ( tSec / tPar ) );

    //
    // Calculo del numero PI de forma paralela:
    // Una acumulacion por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra." );
    t1 = System.nanoTime();

    // (B) ...
    Acumula a2 = new Acumula();
    MiHebraUnaAcumulacion vt2[] =
            new MiHebraUnaAcumulacion[ numHebras ];

    for( int i = 0; i < numHebras; i++ ) {
      vt2[ i ] = new MiHebraUnaAcumulacion(
              i, numHebras, numRectangulos, a2 );
      vt2[ i ].start();
    }
    try {
      for( int i = 0; i < numHebras; i++ ) {
        vt2[ i ].join();
      }
    } catch( InterruptedException ex ) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a2.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + ( tSec / tPar ) );

    //
    // Calculo del numero PI de forma paralela:
    // Multiples acumulaciones por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra (At)." );
    t1 = System.nanoTime();

    // (C) ...
    DoubleAdder a3 = new DoubleAdder();
    MiHebraMultAcumulacionAtomica vt3[] =
            new MiHebraMultAcumulacionAtomica[ numHebras ];

    for( int i = 0; i < numHebras; i++ ) {
      vt3[ i ] = new MiHebraMultAcumulacionAtomica(
              i, numHebras, numRectangulos, a3 );
      vt3[ i ].start();
    }
    try {
      for( int i = 0; i < numHebras; i++ ) {
        vt3[ i ].join();
      }
    } catch( InterruptedException ex ) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a3.sum();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + ( tSec / tPar ) );

    //
    // Calculo del numero PI de forma paralela:
    // Una acumulacion por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra (At)." );
    t1 = System.nanoTime();

    // (D) ...
    DoubleAdder a4 = new DoubleAdder();
    MiHebraUnaAcumulacionAtomica vt4[] =
            new MiHebraUnaAcumulacionAtomica[ numHebras ];

    for( int i = 0; i < numHebras; i++ ) {
      vt4[ i ] = new MiHebraUnaAcumulacionAtomica(
              i, numHebras, numRectangulos, a4 );
      vt4[ i ].start();
    }
    try {
      for( int i = 0; i < numHebras; i++ ) {
        vt4[ i ].join();
      }
    } catch( InterruptedException ex ) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a4.sum();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + ( tSec / tPar ) );

    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

// ===========================================================================
class Acumula {
  // ===========================================================================
  double suma;

  // -------------------------------------------------------------------------
  Acumula() {
    // (E) ...
    suma = 0.0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    // (F) ...
    suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    // (G) ...
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread {
  // ===========================================================================
  int     miId, numHebras;
  long    numRectangulos;
  Acumula a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones( int miId, int numHebras, long numRectangulos,
                            Acumula a ) {
    // (H) ...
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    // (I) ...
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x;

    for( long i = miId; i < numRectangulos; i += numHebras ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      a.acumulaDato( EjemploNumeroPI.f( x ) );
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacion extends Thread {
  // ===========================================================================
  int     miId, numHebras;
  long    numRectangulos;
  Acumula a;

  // -------------------------------------------------------------------------
  MiHebraUnaAcumulacion( int miId, int numHebras, long numRectangulos,
                         Acumula a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, sumaL = 0.0;

    for( long i = miId; i < numRectangulos; i += numHebras ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      sumaL += EjemploNumeroPI.f( x );
    }
    a.acumulaDato( sumaL );
  }
}

// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread {
  // ===========================================================================
  int        miId, numHebras;
  long       numRectangulos;
  DoubleAdder a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulacionAtomica( int miId, int numHebras, long numRectangulos,
                                 DoubleAdder a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x;

    for( long i = miId; i < numRectangulos; i += numHebras ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      a.add( EjemploNumeroPI.f( x ) );
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica extends Thread {
  // ===========================================================================
  int         miId, numHebras;
  long        numRectangulos;
  DoubleAdder a;

  // -------------------------------------------------------------------------
  MiHebraUnaAcumulacionAtomica( int miId, int numHebras, long numRectangulos,
                                DoubleAdder a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, sumaL = 0.0;

    for( long i = miId; i < numRectangulos; i += numHebras ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      sumaL += EjemploNumeroPI.f( x );
    }
    a.add( sumaL );
  }
}
