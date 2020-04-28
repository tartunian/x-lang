program { int i
  i=5
  switch(i) {
    case 1: i=write(1)
    case 2: i=write(2)
    case 3: i=write(3)
    case 4: i=write(4)
    case 5: i=write(5)
    case 6: switch(i) {
      case 6: switch(i) {
        case 6: switch(i) {
          case 6: i=write(6666)
        }
      }
    }
    default: i=write(0)
  }
}