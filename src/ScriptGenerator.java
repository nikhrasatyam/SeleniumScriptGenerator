import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.Data;

public class ScriptGenerator {
	File folder;

	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		System.out.println("Enter the path of input file");
		String inputFilePath = sc.nextLine();
		File folder = new File(inputFilePath);
		ScriptGenerator generator = new ScriptGenerator();

		String[] pathOfJavaFiles = generator.getFileNames(folder);
		// analysis.countNumberOfClasses(pathOfJavaFiles);
		Map<String, String> details = generator.scriptGenerator(pathOfJavaFiles);
		for (String string : details.keySet()) {

			String filename = folder + "/testScripts/" + "test" + string + ".html";
			File file2 = new File(filename);
			FileWriter fileWriter = new FileWriter(file2);
			fileWriter.write(details.get(string));
			fileWriter.flush();
			fileWriter.close();

		}

	}

	public String[] getFileNames(File folderPath) throws IOException {

		File[] listOfFiles = folderPath.listFiles();
		String line;
		int javaFilesCount = 0;
		String[] pathOfFiles = new String[listOfFiles.length];

		for (File file : listOfFiles) {
			if (file.isFile()) {
				if (file.getName().contains("html")) {
					pathOfFiles[javaFilesCount] = file.getPath();
					javaFilesCount++;

					System.out.println(file.getName() + " " + file.getPath());

				}
			}
		}

		return pathOfFiles;
	}

	public Map<String, String> scriptGenerator(String[] pathofFiles) throws IOException {
		String pathOfHomepage = null;
		Map<String, String> fileAndScript = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		for (String string : pathofFiles) {
			if (string != null) {
				if (string.contains("index") || string.contains("home")) {
					pathOfHomepage = string;
				}
				String[] strings = string.split("\\W");
				list.add(strings[strings.length - 2]);

				// System.out.println(string);

			}
		}

		for (String path : pathofFiles) {
			if (path != null) {
				// System.out.println(list);
				// pathOfHomepage="C:/Users/Gamer/Downloads/space-science/upload/index.html";
				String[] string = path.split("\\W");
				String name = string[string.length - 2];
				String element;
				if (name.equals("index")) {
					element = "Home";
				} else {
					element = Character.toUpperCase(name.charAt(0)) + name.substring(1);
				}
				File file = new File(pathOfHomepage);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				StringBuffer stringBuffer = new StringBuffer();
				Pattern pattern = Pattern.compile("\">+(.+)+<");
				Pattern pattern1 = Pattern.compile("href=\"(.+).html.+");
				String line;
				String begining = "&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;!DOCTYPE html PUBLIC &quot;-//W3C//DTD XHTML 1.0 Strict//EN&quot; &quot;http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd&quot;&gt;&lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot; xml:lang=&quot;en&quot; lang=&quot;en&quot;&gt;&lt;head profile=&quot;http://selenium-ide.openqa.org/profiles/test-case&quot;&gt;&lt;meta http-equiv=&quot;Content-Type&quot; content=&quot;text/html; charset=UTF-8&quot; /&gt;&lt;link rel=&quot;selenium.base&quot; href=&quot;https://login.live.com/&quot; /&gt;&lt;title&gt;"
						+ "test" + element
						+ "&lt;/title&gt;&lt;/head&gt;&lt;body&gt;&lt;table cellpadding=&quot;1&quot; cellspacing=&quot;1&quot; border=&quot;1&quot;&gt;&lt;thead&gt;&lt;tr&gt;&lt;td rowspan=&quot;1&quot; colspan=&quot;3&quot;&gt;selenium&lt;/td&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;open&lt;/td&gt;&lt;td&gt;file:///"
						+ path + "&lt;/td&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;";
				// String fin=sdsd.replaceAll("&lt;", "<");
				stringBuffer.append(begining);
				stringBuffer.append("\n");
				while ((line = bufferedReader.readLine()) != null) {
					String pageName = null;
					if (line.contains("href") && line.contains("css"))
						continue;
					if (line.contains("href") && line.contains("img src"))
						continue;
					else if (line.contains("href")) {
						Matcher matcher = pattern.matcher(line);
						Matcher matcher1 = pattern1.matcher(line);
						if (matcher1.find()) {
							pageName = matcher1.group(1);
						}

						while (matcher.find()) {
							String g = matcher.group(1).toLowerCase();
							if (pageName != null && list.contains(pageName)
									&& pageName.equals(matcher.group(1).toLowerCase())) {
								stringBuffer.append("&lt;tr&gt;&lt;td&gt;clickAndWait&lt;/td&gt;&lt;td&gt;link=");
								stringBuffer.append(matcher.group(1));
								stringBuffer.append("&lt;/td&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;");
							} else {
								stringBuffer.append("&lt;tr&gt;&lt;td&gt;clickAndWait&lt;/td&gt;&lt;td&gt;link=");
								stringBuffer.append(element);
								stringBuffer.append("&lt;/td&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;");
								stringBuffer.append("&lt;tr&gt;&lt;td&gt;clickAndWait&lt;/td&gt;&lt;td&gt;link=");
								String[] split = matcher.group(1).split("</a>");
								stringBuffer.append(split[0]);
								// stringBuffer.append(matcher.group(1));
								stringBuffer.append("&lt;/td&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;");
							}
						}
					}
				}
				stringBuffer.append("&lt;/tbody&gt;&lt;/table&gt;&lt;/body&gt;&lt;/html&gt;");
				String seleniumScript = stringBuffer.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replaceAll("&quot", "\"").replaceAll("<span>", "").replaceAll("</span>", "")
						.replaceAll("</a>", "");

				fileReader.close();
				// System.out.println("Contents of file:");
				fileAndScript.put(element, seleniumScript);
			}
		}

		return fileAndScript;
		// return seleniumScript;
	}
}
