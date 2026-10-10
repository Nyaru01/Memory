"""Publish README and linked Markdown documents in one commit, without local secrets."""
from pathlib import Path
from github_release import request, token, API

secret = token()
ref = request(secret, API + "/git/ref/heads/main")
parent = ref["object"]["sha"]
commit = request(secret, API + "/git/commits/" + parent)
files = [Path("README.md"), Path("CHANGELOG.md"), *sorted(Path("docs").glob("*.md"))]
tree = request(secret, API + "/git/trees", "POST", {"base_tree": commit["tree"]["sha"], "tree": [
    {"path": path.as_posix(), "mode": "100644", "type": "blob", "content": path.read_text(encoding="utf-8")} for path in files]})
if tree["sha"] == commit["tree"]["sha"]:
    print("GitHub documentation already current")
else:
    updated = request(secret, API + "/git/commits", "POST", {
        "message": "Document MemoMondes downloads, updates and validation", "tree": tree["sha"], "parents": [parent]})
    request(secret, API + "/git/refs/heads/main", "PATCH", {"sha": updated["sha"], "force": False})
    print("https://github.com/Nyaru01/Memory#readme")
