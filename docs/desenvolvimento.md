# Desenvolvimento e validação

## Comandos

Execute sempre uma tarefa Gradle por vez para evitar concorrência entre os
caches dos loaders:

```powershell
.\gradlew.bat buildAll
.\gradlew.bat :forge1201:build
.\gradlew.bat :neoforge:build
.\gradlew.bat runForgeClient
.\gradlew.bat runNeoForgeClient
.\gradlew.bat runForgeServer
.\gradlew.bat runNeoForgeServer
.\gradlew.bat verifyAll
```

Os diretórios de execução e resultados são separados em `forge1201/runs` e
`neoforge/runs`. Os JARs e sources ficam em `build/libs` de cada módulo.

## Evidências e pendências

| Camada | Evidência atual |
|---|---|
| Dependências Create | JAR local Create 1.20.1 6.0.8 inspecionado; NeoForge usa Create 6.0.10 configurado no Gradle. |
| Build Forge e NeoForge | Pendente: a tentativa local não conseguiu conectar ao daemon Gradle. |
| Boot cliente/servidor | Pendente. |
| Receitas maiores e automação completa por pacotes | Pendente. |
| Persistência, multiplayer, wrench e drops | Pendente. |
| Capturas nos dois loaders e revisão manual | Pendente. |

Uma verificação é considerada aprovada somente depois de produzir relatório novo
para o loader correspondente. Inicialização e compilação não substituem a
validação manual do fluxo de criação.

