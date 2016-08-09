import os
from subprocess import call

root_dir = os.path.dirname(os.path.abspath(__file__))
bin_dir = os.path.join(root_dir, "bin")
if not os.path.isdir(bin_dir):
	os.mkdir(bin_dir)

for source_path, _, files in os.walk(os.path.join(root_dir, "src")):
	java_files = list(filter(lambda x: x.endswith(".java"), files))
	if len(java_files) == 0:
		continue

	destination_path = os.path.join(bin_dir, os.path.relpath(source_path, root_dir))
	java_files = [os.path.join(source_path, name) for name in java_files]
	call(["javac", "-s", source_path, "-d", bin_dir] + java_files)
