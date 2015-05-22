package com.falconraptor.solderJarToZip

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.zip.{ZipEntry, ZipOutputStream}
import javax.swing.JOptionPane

object SolderJarToZip {
  def zipFolder(srcFolder: String, destZipFile: String) {
    val fileWriter: FileOutputStream = new FileOutputStream(destZipFile)
    val zip: ZipOutputStream = new ZipOutputStream(fileWriter)
    addFolderToZip("", srcFolder, zip)
    zip.flush
    zip.close
    fileWriter close()
  }

  def removeAll(path: String) = {
    def getRecursively(f: File): Seq[File] = f.listFiles.filter(_.isDirectory).flatMap(getRecursively) ++ f.listFiles
    getRecursively(new File(path)).foreach { f => if (!f.delete()) throw new RuntimeException("Failed to delete " + f.getAbsolutePath) }
  }

  def main(args: Array[String]) {
    var jar = new File("hi")
    jar = jar.getAbsoluteFile
    for (t <- jar.getParentFile.listFiles() if !t.getName.equals("solderJarToZip.jar") && t.getName.contains(".jar")) {
      var name = t.getName.toLowerCase.replaceAll(" ", "-")
      if (name.lastIndexOf("-") != -1) name = name.substring(0, name.lastIndexOf("-"))
      val f = new File(name)
      f mkdir()
      val temp = new File("mods")
      temp mkdir()
      t renameTo new File(temp, t.getName)
      zipFolder("mods", f.getName + "/" + t.getName.toLowerCase.replace(".jar", ".zip").replaceAll(" ", "-"))
      removeAll(temp.getName)
    }
    new File("mods").delete
    jar delete()
    JOptionPane.showMessageDialog(null, "Done making zips", "Done!", JOptionPane.INFORMATION_MESSAGE)
  }

  private def addFileToZip(path: String, srcFile: String, zip: ZipOutputStream) {
    val folder: File = new File(srcFile)
    if (folder.isDirectory) addFolderToZip(path, srcFile, zip)
    else {
      val buf: Array[Byte] = new Array[Byte](1024)
      var len: Int = 0
      val in: FileInputStream = new FileInputStream(srcFile)
      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName))
      while ( {
        len = in.read(buf);
        len
      } > 0) zip.write(buf, 0, len)
      in close()
    }
  }

  private def addFolderToZip(path: String, srcFolder: String, zip: ZipOutputStream) {
    val folder: File = new File(srcFolder)
    for (fileName <- folder.list) {
      if (path == "") addFileToZip(folder.getName, srcFolder + "/" + fileName, zip)
      else addFileToZip(path + "/" + folder.getName, srcFolder + "/" + fileName, zip)
    }
  }
}
