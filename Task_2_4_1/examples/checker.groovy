// Основной скрипт: импортирует состав курса и задаёт, что именно проверять.
importConfig 'groups.groovy'

checkpoints {
    checkpoint 'АК1', '2026-03-23'
    checkpoint 'АК2', '2026-04-13'
    checkpoint 'Итог', '2026-05-30'
}

assignment {
    check group: '24216', student: 'DevDashkovsky', tasks: ['2_1_1', '2_3_1']
    check group: '24216', student: 'vylegzhaninn', tasks: ['2_1_1']
}

settings {
    testTimeoutMs 120_000
    gradeScale([5: 2.5, 4: 1.5, 3: 0.5, 2: 0.0])
    activityScale([0.8: 1.0, 0.5: 0.9, 0.0: 0.8])
    bonus student: 'vylegzhaninn', task: '2_1_1', points: 0.5
}
