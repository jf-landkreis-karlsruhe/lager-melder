const fetch = require('node-fetch');

// {"firstName":"Skell","lastName":"Rutherfoord","birthday":"25.07.2012"}
const youthData = require('./_youth.json');
// {"firstName":"Pandora","lastName":"Loosely","birthday":"16.02.1993"}
const youthLeaderData = require('./_youthLeader.json');

let totalYouthCounter = 0;
let totalYouthLeaderCounter = 0;
async function main() {
    const token = process.argv[2];
    const baseUrl = process.argv[3];
    if (!token || !baseUrl) {
        console.log('Missing argument: node ./createMockData.js $token $baseUrl');
    }
    await createDataForADepartment('London', 'Sadiq Khan', 10, baseUrl, token);
    await createDataForADepartment('Berlin', 'Michael Müller', 40, baseUrl, token);
    await createDataForADepartment('Paris', 'Anne Hidalgo', 70, baseUrl, token);
    await createDataForADepartment('Karlsruhe', 'Frank Mentrup', 0, baseUrl, token, SPECIALIZED_FIELD_DIRECTOR);
}


async function createDataForADepartment(departmentName, leaderName, numberOfAttendees, baseUrl, token, user = USER) {

    const departmentId = await createDepartment(departmentName, leaderName, baseUrl, token)
        .then(department => department.id)
    await createUser(departmentId, user, baseUrl, token)
    for (i = 0; i < numberOfAttendees * 4 / 5; i++) {
        const youthAttendee = youthData[totalYouthCounter];
        await createAttendee(youthAttendee.firstName, youthAttendee.lastName, departmentId, youthAttendee.birthday, youthAttendee.additionalInformation, 'YOUTH', baseUrl, token);
        totalYouthCounter++;
        if (i % 5) {
            const youthLeader = youthLeaderData[totalYouthLeaderCounter]
            createAttendee(youthLeader.firstName, youthLeader.lastName, departmentId, youthLeader.birthday, youthLeader.additionalInformation, 'YOUTH_LEADER', baseUrl, token);
            totalYouthLeaderCounter++;
        }
    }
    console.log(`Created department ${departmentId} with ${numberOfAttendees} attendees for ${user}.`);
}

function createDepartment(name, leaderName, baseUrl, token) {
    const data = {
        "name": name,
        "leaderName": leaderName,
        "leaderEMail" : `${leaderName.replace(' ', '')}@feuerwehr.de`,
    };
    return post(baseUrl + '/departments', data, token);
}

const SPECIALIZED_FIELD_DIRECTOR = 'SPECIALIZED_FIELD_DIRECTOR';
const USER = 'USER';
function createUser(departmentId, role, baseUrl, token) {
    const data = {
        "departmentId": departmentId,
        "username": `user${departmentId}`,
        "password": `password${departmentId}`,
        "role" : role,
    };
    return post(baseUrl + '/users', data, token);
}

const FOOD = [ 'MEAT', 'NONE', 'ALLERGY', 'VEGETARIAN', 'VEGAN', 'MUSLIM' ];
const T_SHIRT_SIZE = [ 'ONE_HUNDRED_TWENTY_EIGHT', 'ONE_HUNDRED_FORTY', 'ONE_HUNDRED_FIFTY_TWO', 'ONE_HUNDRED_FIFTY_EIGHT', 'ONE_HUNDRED_SIXTY_FOUR', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL', 'XXXXL', 'XXXXXL' ];
function createAttendee(firstName, lastName, departmentId, birthday, additionalInformation, role, baseUrl, token) {
    const food = getRandomValue(FOOD);
    const tShirtSize = getRandomValue(T_SHIRT_SIZE);
    const data = {
        "firstName": firstName,
        "lastName": lastName,
        "departmentId" : departmentId,
        "birthday": birthday,
        "food": food,
        "tShirtSize": tShirtSize,
        "additionalInformation": additionalInformation || '',
        "role": role,
    };
    return post(baseUrl + '/attendees', data, token);
}

function getRandomValue(array) {
    return array[Math.floor(Math.random() * array.length)];
}

function post(url, data, token) {
    return fetch(url, {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(data),
    })
    .then(response => response.json());
}

main();
