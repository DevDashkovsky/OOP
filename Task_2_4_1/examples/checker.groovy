// Основной скрипт: импортирует состав курса и задаёт, что именно проверять.
importConfig 'groups.groovy'

checkpoints {
    checkpoint 'АК1', '2024-11-01'
    checkpoint 'АК2', '2024-12-20'
    checkpoint 'Итог', '2025-01-25'
}

assignment {
    check group: '22213', student: 'ivanov', tasks: ['2_1_1', '2_3_1']
    check group: '22213', student: 'petrov', tasks: ['2_1_1']
}

settings {
    testTimeoutMs 120_000
    gradeScale([5: 2.5, 4: 1.5, 3: 0.5, 2: 0.0])
    activityScale([0.8: 1.0, 0.5: 0.9, 0.0: 0.8])
    bonus student: 'petrov', task: '2_1_1', points: 0.5
}
