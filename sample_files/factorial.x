program { boolean j int i int k int l
  int factorial(int n) {
      if (n < 2) then 
         { return 1 }
      else 
         {return n*factorial(n-1) }
  }
  while (1==1) {
      i = read()
      l = write(factorial(i))
  }
}