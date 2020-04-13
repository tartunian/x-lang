program { int i char c string s
  //i = s > c
  //i = c >= s
  //s = "valid string"
  //c = 'z'

  i = 0
  
  if(true) then {
    i = 1
  } else {
    i = 0
  }

  if(false) then {
    i = 99
  }

  unless(false) then {
    i = 33
  }

  switch(i) {
    case 1: i = 3
    case 2: i = 5
    default: i = 42
  }
  
}