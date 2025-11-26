package ru.nsu.dashkovskii;

/**
 * Запись об оценке в зачетной книжке.
 */
public class Record {
    private final String subjectName;
    private final ControlType controlType;
    private final Grade grade;
    private final int semester;

    /**
     * Конструктор записи.
     *
     * @param subjectName имя предмета
     * @param controlType тип контроля
     * @param grade оценка
     * @param semester номер семестра
     */
    public Record(String subjectName, ControlType controlType, Grade grade, int semester) {
        this.subjectName = subjectName;
        this.controlType = controlType;
        this.grade = grade;
        this.semester = semester;
    }

    /**
     * Получить название предмета.
     *
     * @return название предмета
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Получить тип контроля.
     *
     * @return тип контроля
     */
    public ControlType getControlType() {
        return controlType;
    }

    /**
     * Получить оценку.
     *
     * @return оценка
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Получить номер семестра.
     *
     * @return номер семестра
     */
    public int getSemester() {
        return semester;
    }
}

