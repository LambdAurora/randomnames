package me.lambdaurora.randomnames;

import org.aperlambda.lambdacommon.resources.ResourcesManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class RandomNames
{
	private static final File   NAMES_FILE = new File("names.csv");
	private static final Random RANDOM     = new Random();

	public static void main(String[] args)
	{
		println("Starting RandomNames v1.0.0");
		print("Loading file... ");
		List<String> lines;
		try
		{
			if (!NAMES_FILE.exists())
			{
				URL url = new URL("https://www.data.gouv.fr/s/resources/liste-de-prenoms/20141127-154433/Prenoms.csv");
				InputStream stream = ResourcesManager.getDefaultResourcesManager().getResource(url);
				if (stream == null)
				{
					println("FAILED.");
					return;
				}
				if (ResourcesManager.getDefaultResourcesManager().saveResource(stream, NAMES_FILE.getName(), new File("."), false))
					print("Default resource saved. ");
				else
				{
					println("FAILED (Cannot save default file.");
					return;
				}
				stream.close();
			}
			lines = readFile(NAMES_FILE);
			println("DONE.");
		}
		catch (MalformedURLException e)
		{
			println("FAILED.");
			println("Cannot continue, the URL was malformed...");
			return;
		}
		catch (IOException e)
		{
			println("FAILED.");
			e.printStackTrace();
			return;
		}

		println("Formatting internally lines...");
		var decodedLines = lines.stream().map(line -> Arrays.asList(line.split(";"))).collect(Collectors.toList());

		println("Welcome, please select an action");
		var scanner = new Scanner(System.in);

		String[] input;
		while (!(input = scanner.nextLine().split(" "))[0].equalsIgnoreCase("exit"))
		{
			input[0] = input[0].toLowerCase();
			switch (input[0])
			{
				case "random":
					var gender = '0';
					var origin = new String[0];
					int index = 1;
					if (input.length >= 2)
						while (input.length > index)
						{
							if (input.length > (index + 1))
							{
								if (input[index].equalsIgnoreCase("gender"))
								{
									index++;
									gender = input[index].charAt(0);
								}
								else if (input[index].equalsIgnoreCase("origin"))
								{
									index++;
									origin = input[index].split(",");
								}
							}
							index++;
						}
					printResult(getRandomName(filterByOrigin(filterByGender(decodedLines, gender), origin)));
					break;
				default:
					println("Dafuq?");
					break;
			}
		}
	}

	public static List<String> getRandomName(List<List<String>> names)
	{
		if (names.isEmpty())
			return Arrays.asList("404", "404", "404");
		int line = RANDOM.nextInt(names.size());
		return names.get(line);
	}

	public static List<List<String>> filterByGender(List<List<String>> names, char gender)
	{
		if (gender == 'm' || gender == 'f')
			return names.stream().filter(line -> line.get(1).equals(String.valueOf(gender))).collect(Collectors.toList());
		else if (gender == '0')
			return names;
		else return names.stream().filter(line -> line.get(1).isEmpty()).collect(Collectors.toList());
	}

	public static List<List<String>> filterByOrigin(List<List<String>> names, String[] targetOrigins)
	{
		if (targetOrigins.length == 0)
			return names;
		else
			return names.stream().filter(line -> {
				String[] origins = line.get(2).split(", ");
				for (var origin : origins)
					for (var target : targetOrigins)
						if (target.equalsIgnoreCase(origin))
							return true;
				return false;
			}).collect(Collectors.toList());
	}

	public static List<String> getRandomNameByGender(List<List<String>> names, char gender)
	{
		return getRandomName(filterByGender(names, gender));
	}

	public static void printResult(List<String> line)
	{
		println("Result: " + line.get(0) + " (Origin: " + line.get(2) + ", gender: " + line.get(1) + ")");
	}

	public static List<String> readFile(File file) throws IOException
	{
		List<String> result = new ArrayList<>();

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		for (String line = br.readLine(); line != null; line = br.readLine())
		{
			result.add(line);
		}

		br.close();
		fr.close();

		return result;
	}

	public static void print(Object obj)
	{
		System.out.print(obj);
	}

	public static void println(Object obj)
	{
		System.out.println(obj);
	}
}