# JAVA Framework

## Play!
---
* 2.0에서 점점 Scala쪽으로 넘어가고 있어 JAVA Framework라고 보기 조금 애매함.

## Spring
---

## Jersey
---

## Benchmark
---
출처 : [TechEmpower - Web Framework Benchmarks](https://www.techempower.com/benchmarks/)

* JSON serialization (높을수록 좋음)
	1. **Jersey** : 165,806
	2. **Play 1 (Siena)** : 151,780
	3. **Spring** : 97,354 
* Single query
	1. **Spring** : 55,291
	2. **Jersey** : 53,642
	3. **Play 1 (Siena)** : 19,044
* Multiple queries
	1. **Jersey** : 6,555
	2. **Spring** : 3,916
	3. **Play 1 (Siena)** : 2,922
* Fortunes
	1. **Jersey** : 45,007
	2. **Spring** : 21,807
	3. **Play 1 (Siena)** : N/A
* 결론
	* 성능은 Jersey > Spring > Play!
	* Jersey와 Spring 둘중 하나를 골랴아 되는데, 개인적으로는