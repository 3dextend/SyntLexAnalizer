/*
 * Класс-драйвер для запуска синтаксического и лексического анализатора
 * Рябенький Сергей 2ПЗ-пс
 */
public class DriverProgram {

	public static void main(String[] args) {

		System.out.println("Парсер языка RJ. Разработал Рябенький С.А 2ПЗ-пс");
		System.out.println("My JavaParser 2015");

		if (args.length > 0) {
			System.out.println("Чтение файла с программой " + args[0]);
			Scanner scanner = new Scanner(args[0]);
			System.out.println("Парсинг файла с программой " + args[0]);
			Parser parser = new Parser(scanner);
			System.out.println("Найденные ошибки:");
			parser.Parse();
			if (parser.errors.count == 1){
				System.out.println("-- 1 ошибка обнаружена");
			}
			else
				System.out.println("-- " + parser.errors.count
						+ " ошибок обнаружено");
			System.out.println("Параметры текста программы:");
			System.out.println("Количество строк: " + scanner.t.line);
			System.out.println("Количество столбцов " + scanner.t.col);
			System.out.println("Общее количество символов: " + scanner.t.pos);
			}
		    else
			System.out.println("Ошибка: <Не найден файл с программой>");
	}
}
