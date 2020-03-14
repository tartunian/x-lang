program {
  int i
  int j
  string s
  char c
  unless s==0 then {
    s="string"
  }
  switch (i) {
    case 0:
      if(j==9) then {
        switch (j) {
          case 9: j=4
        }
    }
    case 1: j=1
    default: i=1
  }
  
  s="String" * 2
  c= 'c'
}