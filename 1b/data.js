const users = JSON.parse(localStorage.getItem("users")) || [];
const userList = document.getElementById("userList");

function renderUsers() {
  userList.innerHTML = "";

  users.forEach((user, index) => {
    const li = document.createElement("li");
    li.innerHTML = `
      <div class="user-card">
        <div>
          <p><strong>${user.name}</strong></p>
          <p>${user.email}</p>
        </div>
        <button class="delete-btn" onclick="deleteUser(${index})">Delete</button>
      </div>
    `;
    userList.appendChild(li);
  });
}

function deleteUser(index) {
  users.splice(index, 1);
  localStorage.setItem("users", JSON.stringify(users));
  renderUsers();
}

renderUsers();
