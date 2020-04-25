program { int i char c string s
  //i = s > c
  //i = c >= s
  s = "valid string"
  c = 'z'
  if(1==1) then {
    i = 1
  } else {
    i = 0
  }
  if(1==2) then {
    i = 99
  }
  unless(1==2) then {
    i = 33
  }
  switch(i) {
    case 1: i = 3
    case 2: i = 5
    default: i = 42
  }
}