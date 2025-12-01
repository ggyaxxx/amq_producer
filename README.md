curl http://localhost:8080/browse-queue
curl -X POST "http://localhost:8080/produce?queue=LEADER_FOLLOWER&count=10000"