\# QueryMind



\## A Secure, Measurable Natural-Language-to-SQL Platform



QueryMind is a secure full-stack platform that allows users to query relational databases using natural language.



The system converts natural-language questions into SQL using an LLM, independently validates the generated SQL, enforces access-control policies, executes only permitted read-only queries, logs query activity, collects user feedback, and evaluates Text-to-SQL accuracy.



\## Planned Technology Stack



\### Backend

\- Java 26

\- Spring Boot 4.1.1

\- Maven



\### Frontend

\- React

\- Vite

\- JavaScript



\### Databases

\- MySQL

\- PostgreSQL (optional)



\### AI / NLP

\- Large Language Model

\- Text-to-SQL

\- Claude API

\- Provider abstraction for future local LLM support



\### Security

\- JWT Authentication

\- Role-Based Access Control (RBAC)

\- SQL validation

\- Read-only database access

\- Query auditing



\### Development Tools

\- Git

\- GitHub

\- Docker



\## Core Workflow



User Question

→ Authentication \& Authorization

→ Input Guard

→ Schema Filtering

→ LLM

→ SQL Validation

→ Security Checks

→ Read-only Execution

→ Result

→ Audit Log

→ Feedback \& Evaluation



\## Project Status



🚧 Development started from scratch.

