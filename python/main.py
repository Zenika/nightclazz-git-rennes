from pathlib import Path
from zlib import decompress
from re import search

GIT_DIR = ".git"
OBJECTS_DIR = "objects"

SHA1_BLOB = "95d09f2b10159347eece71399a7e2e907ea3df4f"
SHA1_TREE = "8a3e2535ec71c2e30e2a33e0d16ba95507fd9276"
SHA1_COMMIT = "81f46abdf6b06d8d9b4c6785fff98e540c567326"

def get_repo_root() -> Path:
  path = Path.cwd()
  
  while True:
    if (path / GIT_DIR).is_dir():
      return path
    
    else:
      if path == path.parent:
        raise Exception("Ce dossier n'est pas un repertoire git.")
      
      else :
        path = path.parent


def get_git_dir() -> Path:
  repo_root = get_repo_root()

  return repo_root / GIT_DIR


def get_object_path(sha1: str) -> Path:
  if not is_sha1(sha1):
    raise Exception("Ce sha1 est invalide")
  return get_git_dir() / OBJECTS_DIR / sha1[:2] / sha1[2:]


def is_sha1(sha1: str) -> bool:
  return bool(search(r"^[0-9a-f]{40}$", sha1))


def uncompress_object(sha1: str) -> bytes:
  try:
    with get_object_path(sha1).open(mode="rb") as f:
      return decompress(f.read())
  except IOError:
    raise Exception(f"fatal : {sha1}  is not a git object")
  

def get_object(sha1 : str) :
  header, data = uncompress_object(sha1).split(b"\x00", maxsplit=1)
  object_type, size = header.split()
  assert len(data) == int(size), "Unexpected object length"

  return object_type, data


def parse_tree_entry(raw_bytes: bytes) -> tuple[str, str, str, str]:
  info, bin_sha1 = raw_bytes.split(b"\x00", maxsplit= 1)
  mode, raw_name = info.split(b" ", maxsplit=1)
  sha1 = bin_sha1.hex()
  type, _ = get_object(sha1)
  name = raw_name.decode("utf-8")
  return mode.decode("utf-8"), type.decode("utf-8"), sha1, name


def parse_tree(sha1 : str) -> list:
  type, data = get_object(sha1)
  assert type == b"tree"
  entries = []
  start = 0
  while True:
    try:
      sep_index = data.index(b"\x00", start)
      end = sep_index + 1 + 20
    except ValueError:
      break
    entries.append(parse_tree_entry(data[start:end]))
    start = end

  return entries