program { int j int p
j = 3
switch(j){
    case 1: p = 1
    case 2: p = 2
    case 3: p = j + 3
    default : j = 10
  }

  j = j + p
  unless (j < 3) then { j = 100 + j }
}