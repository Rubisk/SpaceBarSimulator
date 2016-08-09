import os
import sys
import shutil
from subprocess import call

root_dir = os.path.dirname(os.path.abspath(__file__))
bin_dir = os.path.join(root_dir, "bin")
source_dir = os.path.join(root_dir, "src")
meta_dir = os.path.join(root_dir, "META-INF")
assets_dir = os.path.join(root_dir, "assets")
jar_path = os.path.join(root_dir, "pcserver.jar")

verbose = "-v" in sys.argv

if not os.path.isdir(bin_dir):
	os.mkdir(bin_dir)

def clean():
	if verbose:
		print("Cleaning...")
	if os.path.exists(bin_dir):
		shutil.rmtree(bin_dir)

	if not os.path.isdir(bin_dir):
		os.mkdir(bin_dir)

	if os.path.exists(jar_path):
		os.remove(jar_path)


def make_class_files():
	if verbose:
		print("Making class files...")
	for current_dir, _, files in os.walk(source_dir):
		java_files = list(filter(lambda x: x.endswith(".java"), files))
		if len(java_files) == 0:
			continue

		if (verbose):
			for file in java_files:
				print("Compiling " + file)

		destination_path = os.path.relpath(current_dir, source_dir)
		java_file_paths = [os.path.join(current_dir, name) for name in java_files]
				
		call(["javac", "-d", bin_dir] + java_file_paths)

def make_jar():
	shutil.copytree(assets_dir, os.path.join(bin_dir, "assets"))
	shutil.copytree(meta_dir, os.path.join(bin_dir, "META-INF"))
	if verbose:
		print("Creating jar...")
	call(["jar", "cMf", "../pcserver.jar", "*"], cwd="bin")


clean()
make_class_files()
make_jar()


