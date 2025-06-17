# Workout - Gerenciador de Treinos de Musculação

### Contexto

*Aplicativo Android para gerenciamento de treinos de musculação com CRUD completo para as entidades Treino e Exercício, integrando autenticação Firebase, banco de dados Firestore e armazenamento de imagens no Firebase Storage.

Este projeto foi desenvolvido como desafio técnico para demonstrar conhecimentos em arquitetura Android moderna, Firebase e boas práticas de desenvolvimento.


### Requisitos do Aplicativo

O aplicativo contém as seguintes funcionalidades:

**Autenticação de usuário via Firebase Authentication (e-mail e senha).**

**Cadastro, listagem, edição e exclusão de Treinos.**

**Cadastro, listagem, edição e exclusão de Exercícios vinculados a cada treino (relação 1:N).**

**Upload e exibição de imagens para os exercícios, armazenadas no Firebase Storage.**

**Interface baseada em Material Design com foco em usabilidade.**

**Utilização da arquitetura MVVM com LiveData e Coroutines para reatividade e operações assíncronas.**


### Entidades

**Treino**

nome: String

descricao: String

data: Timestamp

**Exercício**

nome: String

imagemUrl: String (URL da imagem no Firebase Storage)

observacoes: String

### Tecnologias Utilizadas

**Kotlin**

**Android Jetpack (ViewModel, LiveData)**

**Firebase Authentication**

**Firebase Firestore**

**Firebase Storage**

**Coroutines**

**Material Design Components**

RecyclerView

### Como executar

Clone este repositório.

Configure seu projeto Firebase:

Crie um projeto no Firebase Console.

Ative Authentication (Email/Password).

Configure Firestore e Storage.

Baixe o arquivo google-services.json e adicione na pasta app/.

Abra o projeto no Android Studio.

Aguarde a sincronização Gradle.

Execute o app em um dispositivo ou emulador.

Realize cadastro/login para acessar as funcionalidades.

Crie treinos e exercícios, faça upload de imagens e gerencie seu plano de treino.

