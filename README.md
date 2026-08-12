
## Running the Project Locally

### 1. Backend Setup

The backend uses a standard Spring Boot setup. By default, it uses the configuration found in `backend/src/main/resources/application.properties`.

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Build and start the Spring Boot application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend server will start on `http://localhost:8080`.*

### 2. Frontend Setup

1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install the dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will start on `http://localhost:5173`. Open this URL in your browser to view the application.*

---

## Running the Test Suites

### Backend Tests
The backend contains a comprehensive test suite (22 tests) using JUnit 5 and Mockito, testing Repositories, Services, and Controllers.

To run the backend tests:
```bash
cd backend
./mvnw test
```

### Frontend Tests
The frontend uses Vitest and React Testing Library for testing the React components and Zustand state store (12 tests).

To run the frontend tests:
```bash
cd frontend
npm run test
```

---

## Starting the Application using Docker / Docker-Compose

You can easily spin up the entire application stack (Frontend, Backend, and a MySQL Database) using Docker Compose.

1. Ensure Docker Desktop (or your Docker daemon) is running.
2. From the **root directory** of the project, run:
   ```bash
   docker-compose up --build
   ```
3. Docker will build the frontend and backend images and start them alongside a MySQL container.
   - **Frontend UI:** Available at `http://localhost:80` (or `http://localhost:5173` if mapped accordingly in your compose file)
   - **Backend API:** Available at `http://localhost:8080`
   - **Database:** Internal network `slt-mysql` on port `3306`

To stop the application and remove the containers, press `Ctrl+C`, then run:
```bash
docker-compose down
```
