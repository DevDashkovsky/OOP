// Семестровая часть: описание групп и студентов.
importConfig 'tasks.groovy'

groups {
    group('22213') {
        student('ivanov') {
            fullName 'Иванов Иван'
            repoUrl 'https://github.com/ivanov/oop.git'
        }
        student('petrov') {
            fullName 'Петров Пётр'
            repoUrl 'https://github.com/petrov/oop.git'
        }
    }
}
