package ru.nsu.dashkovskii.dsl

import groovy.util.DelegatingScript
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.dashkovskii.model.CheckerConfig

/**
 * Корневой делегат DSL. Предоставляет верхнеуровневые блоки:
 * tasks, groups, checkpoints, assignment, settings, importConfig.
 */
class ConfigBuilder {
    final CheckerConfig config = new CheckerConfig()
    File scriptDir

    def tasks(Closure closure) {
        runInContext(closure, new TasksBlock(config: config))
    }

    def groups(Closure closure) {
        runInContext(closure, new GroupsBlock(config: config))
    }

    def checkpoints(Closure closure) {
        runInContext(closure, new CheckpointsBlock(config: config))
    }

    def assignment(Closure closure) {
        runInContext(closure, new AssignmentBlock(config: config))
    }

    def settings(Closure closure) {
        runInContext(closure, new SettingsBlock(settings: config.settings))
    }

    /**
     * Подключает другой DSL-файл, исполняя его в текущем контексте.
     */
    def importConfig(String path) {
        File file = new File(path)
        if (!file.isAbsolute() && scriptDir != null) {
            file = new File(scriptDir, path)
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("Импорт не найден: ${file.absolutePath}")
        }
        def cc = new CompilerConfiguration()
        cc.scriptBaseClass = DelegatingScript.name
        def shell = new GroovyShell(this.class.classLoader, new Binding(), cc)
        def script = (DelegatingScript) shell.parse(file)
        def nested = new ConfigBuilder(scriptDir: file.parentFile)
        // разделять состояние не нужно: импорт должен пополнять текущий конфиг
        // поэтому прокидываем «я», но сохраняем scriptDir для вложенных импортов
        def saved = this.scriptDir
        this.scriptDir = file.parentFile
        try {
            script.setDelegate(this)
            script.run()
        } finally {
            this.scriptDir = saved
        }
    }

    private static void runInContext(Closure closure, Object delegate) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        closure.call()
    }
}
