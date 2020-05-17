# Omuretu Programing Language

This is omuretu programing language.
This is very beatiful and delicious language.

## basic grammar

### val

code
```
val a = 1
print(a)
```

output
```
1
```

### var

code
```
var a = 1
print(a)

a = 199
print(a)
```

output
```
1 
199
```


### array

code
```
val array = [0,2,3,4]

print(array[0])
print(array[3])
```

output
```
0
4
```

### while

code
```
var count = 0
while (count < 2) {
    count = count + 1
}
print(2)
```

output
```
2
```

### for 

code
```
for(i in 1 to 3) {
    print(i)
}
```

output
```
1 
2 
3
```

### if

code
```
if (1 < 2) {
    print(1)
} else {
    print(1)
}
```

output
```
1 
```

### function

code
```
def fib (n: Int) {
    if (n < 2) {
      n
    } else {
      fib(n - 1) + fib(n - 2)
    }
}

print(fib(10))
```

output
```
55
```


### class

code
```
class Fib {
    val fib0 = 0
    val fib1 = 1

    def fib(n: Int): Int {
        if (n == 0) {
            fib0
        } elseif (n == 1) {
            fib1
        } else {
            fib(n - 1) + fib(n - 2)
        }
    }
}

val f = Fib()
val result = f.fib(10)
print (result)
```

output
```
55
```