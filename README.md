ClusterSingleton app is started with SM role will only qualify for sequence manager.
On other nodes of cluster where role is not SM singleton will not be started.


1. Start clusterSingletonApp with SM role
sbt -DPORT=2551 -DROLE="SM" "runMain cluster.ClusterSingletonApp"

2. Start test app with "Other" role (replace actor ref string with actual proxy string)
sbt -DPORT=2554 -DROLE="OTHER" "runMain cluster.TestApp"


Expected result

1. When you kill clusterSingletonApp then counter msgs does not appear on test app console.
2. test app console does not instantiate Counter actor behaviour.
