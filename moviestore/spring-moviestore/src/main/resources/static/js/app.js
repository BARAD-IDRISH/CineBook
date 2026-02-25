(function () {
  const page = document.querySelector('.booking-page');
  if (!page) return;

  const seatApi = page.dataset.seatApi;
  const cinemaSelect = document.getElementById('cinemaId');
  const screenSelect = document.getElementById('screenId');
  const dateInput = document.getElementById('showDate');
  const timeSelect = document.getElementById('showTime');
  const seatGrid = document.getElementById('seatGrid');
  const seatLabelsInput = document.getElementById('seatLabels');
  const summary = document.getElementById('selectedSummary');
  const form = document.getElementById('bookingForm');

  let selectedSeats = new Set();
  let takenSeats = new Set();

  function getTodayIsoDate() {
    const d = new Date();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${month}-${day}`;
  }

  function rowLetter(index) {
    return String.fromCharCode(65 + index);
  }

  function clearSelection() {
    selectedSeats = new Set();
    seatLabelsInput.value = '';
    summary.textContent = 'Selected seats: none';
  }

  function filterScreens() {
    const cinemaId = cinemaSelect.value;
    const options = Array.from(screenSelect.options);
    options.forEach((opt, idx) => {
      if (idx === 0) return;
      const allowed = opt.dataset.cinema === cinemaId;
      opt.disabled = !allowed;
      opt.hidden = !allowed;
    });
    if (!cinemaId || (screenSelect.value && screenSelect.selectedOptions[0]?.dataset?.cinema !== cinemaId)) {
      screenSelect.value = '';
    }
  }

  function filterTimes() {
    const cinemaId = cinemaSelect.value;
    const screenId = screenSelect.value;
    const options = Array.from(timeSelect.options);
    options.forEach((opt, idx) => {
      if (idx === 0) return;
      const matchesCinema = opt.dataset.cinema === cinemaId;
      const matchesScreen = opt.dataset.screen === screenId;
      const allowed = !!cinemaId && !!screenId && matchesCinema && matchesScreen;
      opt.disabled = !allowed;
      opt.hidden = !allowed;
    });
    if (!cinemaId || !screenId || (timeSelect.value && !timeSelect.selectedOptions[0]?.dataset?.screen)) {
      timeSelect.value = '';
    }
  }

  function updateSummary() {
    const items = Array.from(selectedSeats).sort();
    seatLabelsInput.value = items.join(', ');
    summary.textContent = items.length ? `Selected seats: ${items.join(', ')}` : 'Selected seats: none';
  }

  function renderGrid(rows, cols) {
    seatGrid.innerHTML = '';
    for (let r = 0; r < rows; r++) {
      const row = document.createElement('div');
      row.className = 'seat-row';
      const label = document.createElement('span');
      label.className = 'seat-row-label';
      label.textContent = rowLetter(r);
      row.appendChild(label);

      for (let c = 1; c <= cols; c++) {
        const seatLabel = `${rowLetter(r)}${c}`;
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'seat-btn';
        btn.dataset.seat = seatLabel;
        btn.textContent = c;

        if (takenSeats.has(seatLabel)) {
          btn.classList.add('taken');
          btn.disabled = true;
        } else if (selectedSeats.has(seatLabel)) {
          btn.classList.add('selected');
        }

        btn.addEventListener('click', function () {
          if (takenSeats.has(seatLabel)) return;
          if (selectedSeats.has(seatLabel)) {
            selectedSeats.delete(seatLabel);
            btn.classList.remove('selected');
          } else {
            selectedSeats.add(seatLabel);
            btn.classList.add('selected');
          }
          updateSummary();
        });

        row.appendChild(btn);
      }
      seatGrid.appendChild(row);
    }
  }

  async function refreshSeats() {
    const cinemaId = cinemaSelect.value;
    const screenId = screenSelect.value;
    const date = dateInput.value;
    const time = timeSelect.value;

    const selectedScreen = screenSelect.options[screenSelect.selectedIndex];
    const rows = Number(selectedScreen?.dataset?.rows || 0);
    const cols = Number(selectedScreen?.dataset?.cols || 0);

    clearSelection();

    if (!cinemaId || !screenId || !date || !time || !rows || !cols) {
      renderGrid(rows || 0, cols || 0);
      return;
    }

    const query = new URLSearchParams({ cinemaId, screenId, date, time });
    const res = await fetch(`${seatApi}?${query.toString()}`);
    if (!res.ok) {
      takenSeats = new Set();
      renderGrid(rows, cols);
      return;
    }

    const data = await res.json();
    takenSeats = new Set(data.takenSeats || []);
    renderGrid(Number(data.rowsCount || rows), Number(data.colsCount || cols));
  }

  cinemaSelect.addEventListener('change', function () {
    filterScreens();
    filterTimes();
    refreshSeats();
  });
  screenSelect.addEventListener('change', function () {
    filterTimes();
    refreshSeats();
  });
  dateInput.addEventListener('change', refreshSeats);
  timeSelect.addEventListener('change', refreshSeats);

  form.addEventListener('submit', function (e) {
    if (!seatLabelsInput.value.trim()) {
      e.preventDefault();
      alert('Please select at least one seat from the grid.');
    }
  });

  // Make the seat matrix visible by default when options are available.
  if (!cinemaSelect.value && cinemaSelect.options.length > 1) {
    cinemaSelect.selectedIndex = 1;
  }
  if (!dateInput.value) {
    dateInput.value = getTodayIsoDate();
  }

  filterScreens();
  filterTimes();
  refreshSeats();
})();
