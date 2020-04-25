program { boolean boo int i int j string s char c
  int increment(int a) {
    return a+1
  }
  unless s==0 then {
    s="string"
    i=increment(5)
  }
  switch (i) {
    case 0:
      if(j==9) then {
        switch (j) {
          case 9: j=4
        }
      }
    case 1:
      if(j>=8) then {
        j=1
      }
    case 2:
      if(j<=8) then {
        //TODO
      }
    case 3:
      if(0) then {
      }
    default: i=1
  }
  i=write(i)
  i=write(j)
  
}