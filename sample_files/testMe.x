program { int a int b int c int d
   int exponent(int base, int power) {
    int counter int result
    counter = 1
    result = base
    if(power == 0) then {
      return 1
    } else {
        while ( counter < power) {
            result = result * base
            counter = counter + 1
        }
    }
    return result
   }
   int modulo (int n, int divisor) {
    while ( divisor <=  n){
     n = n - divisor
    }
    return n
   }
 //Shout out to Euclid.
   int gcd (int n, int m){
    int remainder
    remainder = 1
    while(0 < remainder){
      remainder = modulo(n, m)
      if(remainder != 0 ) then {
       n = m
       m = remainder
      } else { }
    }
    return m
   }
   int lcm( int n , int m){
    return n * m / gcd( n, m )
   }
   int test (int lcm, int gcd , int multiple1, int multiple2){
    int temp int temp1
    temp = write(lcm*gcd)
    temp1 = write(multiple1 * multiple2)
    if ( (lcm * gcd) == (multiple1 * multiple2) ) then {
       return 1
    } else {
    }
    return 0
   }
   a = write( exponent( read(), read() ) )
   a = write( modulo( read(), read() ) )
   a = read()
   b = read()
   c = write( gcd( a, b ) )
   d = write ( lcm( a, b ) )
   d = write(test(a, b, c, d))
 }