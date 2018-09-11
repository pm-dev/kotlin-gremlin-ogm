from locust import HttpLocust, TaskSet, task
import json

class UserBehavior(TaskSet):
    # def on_start(self):
    #     """ on_start is called when a Locust start before any task is scheduled """
    #     self.login()

    # def on_stop(self):
    #     """ on_stop is called when the TaskSet is stopping """
    #     self.logout()

    # def login(self):
    #     self.client.post("/login", {"username":"ellen_key", "password":"education"})

    # def logout(self):
    #     self.client.post("/logout", {"username":"ellen_key", "password":"education"})

    @task(1)
    def getR2D2(self):
        self.client.post("/graphql", data=json.dumps({"query":"{\n  character(name: \"R2-D2\") {\n    name {\n      full\n    }\n    friends {\n      createdAt\n      name {\n        full\n      }\n      friends {\n        name {\n          full\n        }\n        friends {\n          name {\n            full\n          }\n        }\n      }\n    }\n    secondDegreeFriends(limit: 2) {\n      id\n      name {\n        full\n      }\n    }\n    ... on Human {\n      twinSiblings {\n        name {\n          full\n        }\n      }\n    }\n  }\n}\n"}), headers={'content-type': 'application/json'})        

class WebsiteUser(HttpLocust):
    task_set = UserBehavior
    min_wait = 1000
    max_wait = 5000